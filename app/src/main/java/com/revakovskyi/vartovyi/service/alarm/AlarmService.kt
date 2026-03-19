package com.revakovskyi.vartovyi.service.alarm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.constants.AlarmContract
import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmStateHolder
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.ui.alarm.AlarmActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.atomic.AtomicBoolean

private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "vartovyi_alarm"
private val VIBRATION_PATTERN = longArrayOf(0, 700, 300)
private const val ALARM_TAG = "AlarmService"
private const val RED_ACCENT_COLOR_RES_ID = android.R.color.holo_red_dark
private const val EMPTY_VALUE = ""
private const val ALARM_ACTIVITY_OPEN_RETRY_DELAY_MILLIS = 400L
private const val ALARM_ACTIVITY_OPEN_MAX_RETRIES = 8
private const val DEFAULT_ALARM_DURATION_SECONDS = 60
private const val MILLIS_IN_SECOND = 1000L

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    private var currentSourceChannelName: String = EMPTY_VALUE
    private var currentSourceMessageText: String = EMPTY_VALUE

    private val alarmStateHolder: AlarmStateHolder by inject()
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var openAlarmActivityJob: Job? = null
    private var alarmAutoStopJob: Job? = null

    private val isAlarmActive = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == AlarmContract.ACTION_STOP) {
            stopAlarmSafely()
            return START_NOT_STICKY
        }

        if (!isAlarmActive.compareAndSet(false, true)) {
            ensureForegroundNotification()
            return START_NOT_STICKY
        }

        currentSourceChannelName = intent?.getStringExtra(
            AlarmContract.EXTRA_SOURCE_CHANNEL_NAME
        ).orEmpty()
        currentSourceMessageText = intent?.getStringExtra(
            AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT
        ).orEmpty()

        alarmStateHolder.setRunning(true)

        requestAudioFocus()
        ensureForegroundNotification()
        openAlarmActivityWithRetries()
        startAlarmSound()
        startVibration()
        scheduleAlarmAutoStop()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmStateHolder.setRunning(false)
        isAlarmActive.set(false)

        releaseAudioFocus()
        stopAlarmSound()
        stopVibration()
        openAlarmActivityJob?.cancel()
        alarmAutoStopJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existingChannel != null && (existingChannel.sound != null || existingChannel.shouldVibrate())) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.alarm_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.alarm_channel_description)
            enableVibration(false)
            setSound(null, null)
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun stopAlarmSafely() {
        if (!isAlarmActive.compareAndSet(true, false)) {
            alarmStateHolder.setRunning(false)
            alarmAutoStopJob?.cancel()
            notifyAlarmStopped()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        alarmStateHolder.setRunning(false)
        currentSourceChannelName = EMPTY_VALUE
        currentSourceMessageText = EMPTY_VALUE

        stopAlarmSound()
        stopVibration()
        releaseAudioFocus()
        openAlarmActivityJob?.cancel()
        alarmAutoStopJob?.cancel()

        notifyAlarmStopped()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun scheduleAlarmAutoStop() {
        alarmAutoStopJob?.cancel()
        alarmAutoStopJob = serviceScope.launch {
            val alarmDurationMillis = resolveAlarmDurationMillis()
            delay(alarmDurationMillis)
            stopAlarmSafely()
        }
    }

    private suspend fun resolveAlarmDurationMillis(): Long {
        val alarmDurationSeconds = runCatching {
            observeScheduleSettingsUseCase().first().alarmDurationSeconds
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to read alarm duration, fallback to default", throwable)
        }.getOrDefault(DEFAULT_ALARM_DURATION_SECONDS)

        return alarmDurationSeconds
            .coerceAtLeast(1)
            .toLong() * MILLIS_IN_SECOND
    }

    private fun notifyAlarmStopped() {
        sendBroadcast(
            Intent(AlarmContract.ACTION_ALARM_STOPPED).setPackage(packageName)
        )
    }

    private fun ensureForegroundNotification(): Boolean {
        return runCatching {
            val notification = buildNotification()
            startForeground(NOTIFICATION_ID, notification)
            true
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to start foreground notification", throwable)
        }.getOrDefault(false)
    }

    private fun canUseFullScreenIntent(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
    }

    private fun openAlarmActivityWithRetries() {
        openAlarmActivityJob?.cancel()
        openAlarmActivityJob = serviceScope.launch {
            for (retryCount in 0..ALARM_ACTIVITY_OPEN_MAX_RETRIES) {
                if (AlarmActivity.isVisible.value) return@launch

                runCatching {
                    startActivity(createAlarmActivityIntent())
                }.onFailure { throwable ->
                    Log.e(ALARM_TAG, "Failed to open alarm activity", throwable)
                }

                if (retryCount < ALARM_ACTIVITY_OPEN_MAX_RETRIES) {
                    delay(ALARM_ACTIVITY_OPEN_RETRY_DELAY_MILLIS)
                }
            }
        }
    }

    private fun createAlarmActivityIntent(): Intent {
        return Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmContract.EXTRA_SOURCE_CHANNEL_NAME, currentSourceChannelName)
            putExtra(AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT, currentSourceMessageText)
        }
    }

    private fun buildNotification(): Notification {
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            createAlarmActivityIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AlarmService::class.java).apply { action = AlarmContract.ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(getString(R.string.alarm_notification_title))
            .setContentText(getString(R.string.alarm_notification_text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColor(ContextCompat.getColor(this, RED_ACCENT_COLOR_RES_ID))
            .setColorized(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(fullScreenPendingIntent)
            .addAction(0, getString(R.string.alarm_stop), stopPendingIntent)
            .setOngoing(true)

        applyFullScreenIntentIfAllowed(
            notificationBuilder = notificationBuilder,
            fullScreenPendingIntent = fullScreenPendingIntent,
        )

        return notificationBuilder.build()
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun applyFullScreenIntentIfAllowed(
        notificationBuilder: NotificationCompat.Builder,
        fullScreenPendingIntent: PendingIntent,
    ) {
        if (!canUseFullScreenIntent()) return

        notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
    }

    private fun requestAudioFocus() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .build()

        audioFocusRequest = focusRequest
        getSystemService(AudioManager::class.java).requestAudioFocus(focusRequest)
    }

    private fun releaseAudioFocus() {
        audioFocusRequest?.let {
            getSystemService(AudioManager::class.java).abandonAudioFocusRequest(it)
        }
        audioFocusRequest = null
    }

    private fun startAlarmSound() {
        if (mediaPlayer?.isPlaying == true) return

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: return

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, alarmUri)
                isLooping = true
                setOnPreparedListener { player ->
                    player.start()
                }
                setOnErrorListener { player, what, extra ->
                    Log.e(ALARM_TAG, "MediaPlayer error: what=$what extra=$extra")
                    runCatching { player.release() }
                    mediaPlayer = null
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(ALARM_TAG, "startAlarmSound error", e)
            mediaPlayer = null
        }
    }

    private fun stopAlarmSound() {
        runCatching { mediaPlayer?.stop() }

        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
    }

    private fun startVibration() {
        if (vibrator?.hasVibrator() == true) {
            return
        }

        val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 0)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        vibrator?.vibrate(vibrationEffect)
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

}
