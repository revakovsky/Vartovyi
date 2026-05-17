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
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.constants.AlarmContract
import com.revakovskyi.vartovyi.controllers.alarm.AlarmStateHolder
import com.revakovskyi.vartovyi.ui.alarm.AlarmActivity
import com.revakovskyi.vartovyi.usecase.settings.ObserveScheduleSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "vartovyi_alarm"
private val VIBRATION_PATTERN = longArrayOf(0, 700, 300)
private val HEADS_UP_TRIGGER_VIBRATION = longArrayOf(0, 1)
private const val ALARM_TAG = "AlarmService"
private const val RED_ACCENT_COLOR_RES_ID = android.R.color.holo_red_dark
private const val EMPTY_VALUE = ""
private const val ALARM_ACTIVITY_OPEN_RETRY_DELAY_MILLIS = 500L
private const val ALARM_ACTIVITY_OPEN_MAX_RETRIES = 2
private const val DEFAULT_ALARM_DURATION_SECONDS = 60
private const val DEFAULT_ALARM_VOLUME_PERCENT = 100
private const val PERCENT_DIVISOR = 100f
private const val MILLIS_IN_SECOND = 1000L
private const val SETTINGS_READ_TIMEOUT_MILLIS = 2_000L
private const val WAKE_LOCK_TAG = "Vartovyi:AlarmWakeLock"
private const val WAKE_LOCK_BUFFER_MILLIS = 15_000L
private const val INITIAL_WAKE_LOCK_TIMEOUT_MILLIS = 30_000L
private const val SCREEN_WAKE_LOCK_TAG = "Vartovyi:ScreenWake"
private const val SCREEN_WAKE_LOCK_TIMEOUT_MILLIS = 10_000L

@Suppress("TooGenericExceptionCaught")
class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var screenWakeLock: PowerManager.WakeLock? = null

    private var currentSourceChannelName: String = EMPTY_VALUE
    private var currentSourceMessageText: String = EMPTY_VALUE
    private var currentAlarmVolume: Float = DEFAULT_ALARM_VOLUME_PERCENT / PERCENT_DIVISOR
    private var currentAlarmSoundUri: Uri? = null

    private val alarmStateHolder: AlarmStateHolder by inject()
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var openAlarmActivityJob: Job? = null
    private var alarmAutoStopJob: Job? = null

    private val isAlarmActive = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ensureForegroundNotification()

        if (intent?.action == AlarmContract.ACTION_STOP) {
            stopAlarmSafely()
            return START_NOT_STICKY
        }

        if (!isAlarmActive.compareAndSet(false, true)) {
            ensureForegroundNotification()
            return START_NOT_STICKY
        }

        acquireWakeLock(INITIAL_WAKE_LOCK_TIMEOUT_MILLIS)
        AlarmControllerImpl.releaseServiceStartWakeLock()
        wakeUpScreen()

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
        startAlarmSoundWithResolvedSettings()
        startVibration()
        scheduleAlarmAutoStop()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmStateHolder.setRunning(false)
        isAlarmActive.set(false)

        releaseScreenWakeLock()
        releaseWakeLock()
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
        if (
            existingChannel != null &&
            (existingChannel.sound != null || !existingChannel.shouldVibrate())
        ) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.alarm_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.alarm_channel_description)
            enableVibration(true)
            vibrationPattern = HEADS_UP_TRIGGER_VIBRATION
            setSound(null, null)
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun stopAlarmSafely() {
        if (!isAlarmActive.compareAndSet(true, false)) {
            alarmStateHolder.setRunning(false)
            alarmAutoStopJob?.cancel()
            releaseScreenWakeLock()
            releaseWakeLock()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        alarmStateHolder.setRunning(false)
        currentSourceChannelName = EMPTY_VALUE
        currentSourceMessageText = EMPTY_VALUE

        stopAlarmSound()
        stopVibration()
        releaseScreenWakeLock()
        releaseWakeLock()
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
            if (!isAlarmActive.get()) return@launch

            val wakeLockTimeoutMillis = alarmDurationMillis + WAKE_LOCK_BUFFER_MILLIS
            acquireWakeLock(wakeLockTimeoutMillis)
            if (!isAlarmActive.get()) {
                releaseWakeLock()
                return@launch
            }

            delay(alarmDurationMillis)
            stopAlarmSafely()
        }
    }

    private suspend fun resolveAlarmDurationMillis(): Long {
        val alarmDurationSeconds = runCatching {
            withTimeoutOrNull(SETTINGS_READ_TIMEOUT_MILLIS) {
                observeScheduleSettingsUseCase().first().alarmDurationSeconds
            } ?: DEFAULT_ALARM_DURATION_SECONDS
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to read alarm duration, fallback to default", throwable)
        }.getOrDefault(DEFAULT_ALARM_DURATION_SECONDS)

        return alarmDurationSeconds
            .coerceAtLeast(1)
            .toLong() * MILLIS_IN_SECOND
    }

    private fun startAlarmSoundWithResolvedSettings() {
        serviceScope.launch {
            currentAlarmVolume = resolveAlarmVolume()
            currentAlarmSoundUri = resolveAlarmSoundUri()
            if (!isAlarmActive.get()) return@launch
            startAlarmSound()
        }
    }

    private suspend fun resolveAlarmVolume(): Float {
        val alarmVolumePercent = runCatching {
            withTimeoutOrNull(SETTINGS_READ_TIMEOUT_MILLIS) {
                observeScheduleSettingsUseCase().first().alarmVolumePercent
            } ?: DEFAULT_ALARM_VOLUME_PERCENT
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to read alarm volume, fallback to default", throwable)
        }.getOrDefault(DEFAULT_ALARM_VOLUME_PERCENT)

        return alarmVolumePercent
            .coerceIn(0, 100)
            .toFloat() / PERCENT_DIVISOR
    }

    private suspend fun resolveAlarmSoundUri(): Uri? {
        val alarmSoundUri = runCatching {
            withTimeoutOrNull(SETTINGS_READ_TIMEOUT_MILLIS) {
                observeScheduleSettingsUseCase().first().alarmSoundUri
            } ?: EMPTY_VALUE
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to read alarm sound uri, fallback to default", throwable)
        }.getOrDefault(EMPTY_VALUE)

        val selectedAlarmSoundUri = alarmSoundUri.takeIf { it.isNotBlank() }?.toUri()

        return selectedAlarmSoundUri ?: resolveDefaultAlarmSoundUri()
    }

    private fun resolveDefaultAlarmSoundUri(): Uri? {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
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
                if (alarmStateHolder.isVisible.value) return@launch

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
            .setOnlyAlertOnce(false)
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

        val focusResult = getSystemService(AudioManager::class.java).requestAudioFocus(focusRequest)
        if (focusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(ALARM_TAG, "Audio focus not granted: $focusResult")
        }
    }

    private fun acquireWakeLock(timeoutMillis: Long) {
        if (!isAlarmActive.get()) return

        releaseWakeLock()

        val powerManager = getSystemService(PowerManager::class.java)
        val newWakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKE_LOCK_TAG,
        ).apply { setReferenceCounted(false) }

        try {
            newWakeLock.acquire(timeoutMillis)
            if (!isAlarmActive.get()) {
                if (newWakeLock.isHeld) {
                    newWakeLock.release()
                }
                return
            }
            wakeLock = newWakeLock
        } catch (throwable: Throwable) {
            Log.e(ALARM_TAG, "Failed to acquire wake lock", throwable)
        }
    }

    private fun releaseWakeLock() {
        val currentWakeLock = wakeLock ?: return

        try {
            if (currentWakeLock.isHeld) currentWakeLock.release()
        } catch (throwable: Throwable) {
            Log.e(ALARM_TAG, "Failed to release wake lock", throwable)
        }

        wakeLock = null
    }

    @Suppress("DEPRECATION")
    private fun wakeUpScreen() {
        val powerManager = getSystemService(PowerManager::class.java)
        if (powerManager.isInteractive) return

        try {
            screenWakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        or PowerManager.ACQUIRE_CAUSES_WAKEUP
                        or PowerManager.ON_AFTER_RELEASE,
                SCREEN_WAKE_LOCK_TAG,
            ).apply {
                setReferenceCounted(false)
                acquire(SCREEN_WAKE_LOCK_TIMEOUT_MILLIS)
            }
        } catch (throwable: Throwable) {
            Log.e(ALARM_TAG, "Failed to acquire screen wake lock", throwable)
        }
    }

    private fun releaseScreenWakeLock() {
        try {
            screenWakeLock?.let { lock ->
                if (lock.isHeld) lock.release()
            }
        } catch (throwable: Throwable) {
            Log.e(ALARM_TAG, "Failed to release screen wake lock", throwable)
        }

        screenWakeLock = null
    }

    private fun releaseAudioFocus() {
        audioFocusRequest?.let {
            getSystemService(AudioManager::class.java).abandonAudioFocusRequest(it)
        }
        audioFocusRequest = null
    }

    private fun startAlarmSound() {
        if (!isAlarmActive.get()) return

        if (mediaPlayer?.isPlaying == true) return

        val alarmUri = currentAlarmSoundUri ?: resolveDefaultAlarmSoundUri() ?: return

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
                setVolume(currentAlarmVolume, currentAlarmVolume)
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
        } catch (e: IOException) {
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
        if (vibrator == null) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
        }

        if (vibrator?.hasVibrator() != true) return

        val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 0)
        vibrator?.vibrate(vibrationEffect)
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

}
