package com.revakovskyi.vartovyi.service

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
import com.revakovskyi.vartovyi.ui.alarm.AlarmActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "vartovyi_alarm"
private val VIBRATION_PATTERN = longArrayOf(0, 700, 300)
private const val ALARM_TAG = "AlarmService"
private const val RED_ACCENT_COLOR_RES_ID = android.R.color.holo_red_dark
private const val EMPTY_KEYWORD = ""

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var currentMatchedKeyword: String = EMPTY_KEYWORD

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAlarmSafely()
            return START_NOT_STICKY
        }

        if (!isAlarmActive.compareAndSet(false, true)) {
            ensureForegroundNotification()
            return START_NOT_STICKY
        }

        currentMatchedKeyword = intent?.getStringExtra(EXTRA_MATCHED_KEYWORD) ?: EMPTY_KEYWORD
        _isRunning.value = true
        requestAudioFocus()
        ensureForegroundNotification()
        startAlarmSound()
        startVibration()
        openAlarmActivity()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        _isRunning.value = false
        isAlarmActive.set(false)

        releaseAudioFocus()
        stopAlarmSound()
        stopVibration()
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
            _isRunning.value = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        _isRunning.value = false
        currentMatchedKeyword = EMPTY_KEYWORD

        stopAlarmSound()
        stopVibration()
        releaseAudioFocus()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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

    private fun openAlarmActivity() {
        if (AlarmActivity.isVisible.value) return

        runCatching {
            val alarmActivityIntent = Intent(this, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_MATCHED_KEYWORD, currentMatchedKeyword)
            }
            startActivity(alarmActivityIntent)
        }.onFailure { throwable ->
            Log.e(ALARM_TAG, "Failed to open alarm activity", throwable)
        }
    }

    private fun buildNotification(): Notification {
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MATCHED_KEYWORD, currentMatchedKeyword)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AlarmService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val canUseFullScreenIntent = canUseFullScreenIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(getString(R.string.alarm_notification_title))
            .setContentText(getString(R.string.alarm_notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(this, RED_ACCENT_COLOR_RES_ID))
            .setColorized(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(fullScreenPendingIntent)
            .apply {
                if (canUseFullScreenIntent) setFullScreenIntent(
                    fullScreenPendingIntent,
                    true
                )
            }
            .addAction(0, getString(R.string.alarm_stop), stopPendingIntent)
            .setOngoing(true)
            .build()
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
                prepare()
                start()
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

    companion object {
        const val ACTION_STOP = "com.revakovskyi.vartovyi.ACTION_STOP_ALARM"
        const val EXTRA_MATCHED_KEYWORD = "com.revakovskyi.vartovyi.EXTRA_MATCHED_KEYWORD"

        private val isAlarmActive = AtomicBoolean(false)
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    }

}
