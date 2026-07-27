package com.revakovskyi.vartovyi.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.revakovskyi.vartovyi.constants.AlarmContract
import com.revakovskyi.vartovyi.service.alarm.AlarmService
import com.revakovskyi.vartovyi.ui.alarm.components.AlarmLandscapeContent
import com.revakovskyi.vartovyi.ui.alarm.components.AlarmPortraitContent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import java.util.concurrent.atomic.AtomicBoolean

private const val ALARM_SCREEN_PULSE_SCALE_MIN = 0.93f
private const val ALARM_SCREEN_PULSE_SCALE_MAX = 1.09f
private const val ALARM_SCREEN_PULSE_DURATION_MS = 1350
private const val EMPTY_VALUE = ""

class AlarmActivity : ComponentActivity() {

    private var sourceChannelName by mutableStateOf(EMPTY_VALUE)
    private var sourceMessageText by mutableStateOf(EMPTY_VALUE)

    private var isAlarmStopReceiverRegistered = false
    private val isDismissing = AtomicBoolean(false)

    private val alarmStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AlarmContract.ACTION_ALARM_STOPPED) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        configureAlarmWindow()
        registerAlarmStopReceiver()
        updateAlarmContentFromIntent(intent)

        setContent {
            VartovyiTheme {
                AlarmContent(
                    sourceChannelName = sourceChannelName,
                    sourceMessageText = sourceMessageText,
                    onDismiss = ::dismissAlarm,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        isDismissing.set(false)
        updateAlarmContentFromIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterAlarmStopReceiver()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
                -> {
                dismissAlarm()
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun configureAlarmWindow() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun dismissAlarm() {
        if (!isDismissing.compareAndSet(false, true)) return

        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmContract.ACTION_STOP
        }
        ContextCompat.startForegroundService(this, stopIntent)
        finishAndRemoveTask()
    }

    private fun updateAlarmContentFromIntent(intent: Intent?) {
        sourceChannelName =
            intent?.getStringExtra(AlarmContract.EXTRA_SOURCE_CHANNEL_NAME).orEmpty()
        sourceMessageText =
            intent?.getStringExtra(AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT).orEmpty()
    }

    private fun registerAlarmStopReceiver() {
        if (isAlarmStopReceiverRegistered) return

        val intentFilter = IntentFilter(AlarmContract.ACTION_ALARM_STOPPED)
        ContextCompat.registerReceiver(
            this,
            alarmStopReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        isAlarmStopReceiverRegistered = true
    }

    private fun unregisterAlarmStopReceiver() {
        if (!isAlarmStopReceiverRegistered) return

        unregisterReceiver(alarmStopReceiver)
        isAlarmStopReceiverRegistered = false
    }

}

@Composable
private fun AlarmContent(
    sourceChannelName: String,
    sourceMessageText: String,
    onDismiss: () -> Unit,
) {
    val windowSize = LocalWindowInfo.current.containerSize

    var alarmContentRootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var alarmIconCenterInContent by remember { mutableStateOf<Offset?>(null) }

    val isTwoPaneLayout = windowSize.width > windowSize.height

    val alarmScreenPulseTransition = rememberInfiniteTransition(label = "alarm_screen_pulse")

    val alarmScreenPulseScale by alarmScreenPulseTransition.animateFloat(
        initialValue = ALARM_SCREEN_PULSE_SCALE_MIN,
        targetValue = ALARM_SCREEN_PULSE_SCALE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ALARM_SCREEN_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alarm_screen_pulse_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                alarmContentRootCoordinates = coordinates
            }
    ) {
        AlarmScreenAnimatedBackground(
            effectCenterInParent = alarmIconCenterInContent,
            modifier = Modifier.fillMaxSize(),
        )

        if (isTwoPaneLayout) {
            AlarmLandscapeContent(
                sourceChannelName = sourceChannelName,
                sourceMessageText = sourceMessageText,
                pulseScale = alarmScreenPulseScale,
                rootCoordinates = { alarmContentRootCoordinates },
                onDismiss = onDismiss,
                onIconCenterInRootChanged = { center -> alarmIconCenterInContent = center },
            )
        } else {
            AlarmPortraitContent(
                sourceChannelName = sourceChannelName,
                sourceMessageText = sourceMessageText,
                pulseScale = alarmScreenPulseScale,
                rootCoordinates = { alarmContentRootCoordinates },
                onDismiss = onDismiss,
                onIconCenterInRootChanged = { center -> alarmIconCenterInContent = center },
            )
        }
    }
}

@Preview(
    name = "Alarm — channel + message",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentChannelAndMessagePreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "Тривога в Харкові",
            sourceMessageText = "Увага! Повітряна небезпека в області, пройдіть в укриття.",
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Alarm — title only",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentTitleOnlyPreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "",
            sourceMessageText = "",
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Alarm — channel only",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentChannelOnlyPreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "Повітряна тривога • Київ",
            sourceMessageText = "",
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Alarm — long message (scroll)",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentLongMessagePreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "Тривога в Харківській області",
            sourceMessageText = "Увага! Оголошено повітряну тривогу по всій області. " +
                    "Зафіксовано зліт ворожої авіації та пуски крилатих ракет у напрямку регіону. " +
                    "Негайно пройдіть до найближчого укриття та залишайтесь там до відбою. " +
                    "Не нехтуйте власною безпекою, тримайтеся подалі від вікон та зовнішніх стін. " +
                    "Слідкуйте за офіційними повідомленнями та не поширюйте неперевірену інформацію. " +
                    "Відбій тривоги буде оголошено окремо — дочекайтесь сигналу про завершення небезпеки.",
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Alarm — small screen, long message",
    widthDp = 320,
    heightDp = 480,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentSmallScreenLongMessagePreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "Тривога в Харківській області",
            sourceMessageText = "Увага! Оголошено повітряну тривогу по всій області. " +
                    "Зафіксовано пуски крилатих ракет у напрямку регіону. " +
                    "Негайно пройдіть до найближчого укриття та залишайтесь там до відбою.",
            onDismiss = {},
        )
    }
}
