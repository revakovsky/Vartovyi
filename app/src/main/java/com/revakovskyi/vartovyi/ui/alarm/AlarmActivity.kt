package com.revakovskyi.vartovyi.ui.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.service.AlarmService
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ALARM_ICON_SIZE_DP = 128
private const val DISMISS_BUTTON_MIN_WIDTH_DP = 200
private const val DISMISS_BUTTON_WIDTH_FRACTION = 0.7f

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()

        setContent {
            VartovyiTheme {
                AlarmContent(onDismiss = ::dismissAlarm)
            }
        }
    }

    private fun setupWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun dismissAlarm() {
        startService(
            Intent(this, AlarmService::class.java).apply {
                action = AlarmService.ACTION_STOP
            }
        )
        finish()
    }

}

@Composable
private fun AlarmContent(
    onDismiss: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.huge))

        Text(
            text = stringResource(R.string.alarm_title),
            style = VartovyiTheme.typography.headlineLarge,
            color = VartovyiTheme.colors.error,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.huge))

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
        AlarmContent(onDismiss = {})
    }
}
