package com.revakovskyi.vartovyi.ui.alarm.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val CONTENT_EDGE_SPACER_WEIGHT = 0.2f

@Composable
internal fun AlarmPortraitContent(
    modifier: Modifier = Modifier,
    sourceChannelName: String,
    sourceMessageText: String,
    pulseScale: Float,
    rootCoordinates: () -> LayoutCoordinates?,
    onDismiss: () -> Unit,
    onIconCenterInRootChanged: (center: Offset) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(VartovyiTheme.spacing.standard)
    ) {
        Spacer(modifier = Modifier.weight(CONTENT_EDGE_SPACER_WEIGHT))

        AlarmPulsingIcon(
            pulseScale = pulseScale,
            rootCoordinates = rootCoordinates,
            onIconCenterInRootChanged = onIconCenterInRootChanged,
        )

        AlarmDetails(
            sourceChannelName = sourceChannelName,
            sourceMessageText = sourceMessageText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = VartovyiTheme.spacing.standard)
        )

        AlarmDismissButton(
            pulseScale = pulseScale,
            onDismiss = onDismiss,
        )

        Spacer(modifier = Modifier.weight(CONTENT_EDGE_SPACER_WEIGHT))
    }
}

@Preview(
    name = "Alarm portrait",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmPortraitContentPreview() {
    VartovyiTheme {
        AlarmPortraitContent(
            sourceChannelName = "Повітряна тривога • Харківська область",
            sourceMessageText = "Увага! Оголошено повітряну тривогу. Негайно пройдіть до " +
                    "найближчого укриття та залишайтеся там до відбою.",
            pulseScale = 1.05f,
            rootCoordinates = { null },
            onDismiss = {},
            onIconCenterInRootChanged = {},
        )
    }
}
