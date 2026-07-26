package com.revakovskyi.vartovyi.ui.alarm.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val DISMISS_BUTTON_MIN_WIDTH_DP = 200
private const val DISMISS_BUTTON_WIDTH_FRACTION = 0.7f

@Composable
internal fun AlarmDismissButton(
    modifier: Modifier = Modifier,
    pulseScale: Float,
    onDismiss: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            onDismiss()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = VartovyiTheme.colors.errorContainer,
            contentColor = VartovyiTheme.colors.onErrorContainer,
        ),
        modifier = modifier
            .widthIn(min = DISMISS_BUTTON_MIN_WIDTH_DP.dp)
            .fillMaxWidth(DISMISS_BUTTON_WIDTH_FRACTION)
            .height(VartovyiTheme.spacing.massive)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
    ) {
        Text(
            text = stringResource(R.string.alarm_dismiss),
            style = VartovyiTheme.typography.titleMedium,
        )
    }
}

@Preview(name = "Alarm dismiss button", showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun AlarmDismissButtonPreview() {
    VartovyiTheme {
        AlarmDismissButton(
            pulseScale = 1f,
            onDismiss = {},
        )
    }
}
