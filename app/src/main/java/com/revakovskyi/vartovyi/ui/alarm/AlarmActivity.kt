package com.revakovskyi.vartovyi.ui.alarm

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.constants.AlarmContract
import com.revakovskyi.vartovyi.service.alarm.AlarmService
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ALARM_ICON_SIZE_DP = 128
private const val DISMISS_BUTTON_MIN_WIDTH_DP = 200
private const val DISMISS_BUTTON_WIDTH_FRACTION = 0.7f
private const val EMPTY_VALUE = ""

class AlarmActivity : ComponentActivity() {

    private var sourceChannelName by mutableStateOf(EMPTY_VALUE)
    private var sourceMessageText by mutableStateOf(EMPTY_VALUE)

    private var isAlarmStopReceiverRegistered = false

    private val alarmStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AlarmContract.ACTION_ALARM_STOPPED) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
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
        updateAlarmContentFromIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        dismissKeyguardIfPossible()
        registerAlarmStopReceiver()
        isVisible.value = true
    }

    override fun onStop() {
        super.onStop()
        unregisterAlarmStopReceiver()
        isVisible.value = false
    }

    private fun setupWindow() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    }

    private fun dismissAlarm() {
        startService(
            Intent(this, AlarmService::class.java).apply {
                action = AlarmContract.ACTION_STOP
            }
        )
        finish()
    }

    private fun updateAlarmContentFromIntent(intent: Intent?) {
        sourceChannelName =
            intent?.getStringExtra(AlarmContract.EXTRA_SOURCE_CHANNEL_NAME).orEmpty()
        sourceMessageText =
            intent?.getStringExtra(AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT).orEmpty()
    }

    private fun dismissKeyguardIfPossible() {
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager.requestDismissKeyguard(this, null)
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

    companion object {
        val isVisible: MutableState<Boolean> = mutableStateOf(false)
    }

}

@Composable
private fun AlarmContent(
    sourceChannelName: String,
    sourceMessageText: String,
    onDismiss: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .padding(VartovyiTheme.spacing.standard),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.alarm),
            contentDescription = null,
            tint = VartovyiTheme.colors.error,
            modifier = Modifier.size(ALARM_ICON_SIZE_DP.dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.alarm_title),
                style = VartovyiTheme.typography.headlineLarge,
                color = VartovyiTheme.colors.error,
                textAlign = TextAlign.Center,
            )

            if (sourceChannelName.isNotBlank() || sourceMessageText.isNotBlank()) {
                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

                if (sourceChannelName.isNotBlank()) {
                    Text(
                        text = sourceChannelName,
                        style = VartovyiTheme.typography.titleLarge,
                        color = VartovyiTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                if (sourceMessageText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

                    Text(
                        text = sourceMessageText,
                        style = VartovyiTheme.typography.bodyLarge,
                        color = VartovyiTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
                containerColor = VartovyiTheme.colors.errorContainer,
                contentColor = VartovyiTheme.colors.onErrorContainer,
            ),
            modifier = Modifier
                .widthIn(min = DISMISS_BUTTON_MIN_WIDTH_DP.dp)
                .fillMaxWidth(DISMISS_BUTTON_WIDTH_FRACTION)
                .height(VartovyiTheme.spacing.massive),
        ) {
            Text(
                text = stringResource(R.string.alarm_dismiss),
                style = VartovyiTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(
    name = "Alarm screen",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmContentPreview() {
    VartovyiTheme {
        AlarmContent(
            sourceChannelName = "Тривога в Харкові",
            sourceMessageText = "Увага! Повітряна небезпека в області, пройдіть в укриття.",
            onDismiss = {},
        )
    }
}
