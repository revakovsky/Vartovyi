package com.revakovskyi.vartovyi.ui.alarm.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.alarm.utils.Constants.ALARM_ICON_MIN_SIZE_DP
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ICON_SIZE_FRACTION = 0.42f

/** Higher than the portrait cap — a landscape tablet has the height to spare */
private const val ICON_LANDSCAPE_MAX_SIZE_DP = 200
private const val ICON_AREA_WEIGHT = 6f
private const val DISMISS_AREA_WEIGHT = 4f

@Composable
internal fun AlarmLandscapeContent(
    modifier: Modifier = Modifier,
    sourceChannelName: String,
    sourceMessageText: String,
    pulseScale: Float,
    rootCoordinates: () -> LayoutCoordinates?,
    onDismiss: () -> Unit,
    onIconCenterInRootChanged: (center: Offset) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .displayCutoutPadding()
            .padding(VartovyiTheme.spacing.standard)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            AlarmDetails(
                sourceChannelName = sourceChannelName,
                sourceMessageText = sourceMessageText,
                modifier = Modifier
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .fillMaxHeight()
            )
        }

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            val iconSize = (maxHeight * ICON_SIZE_FRACTION)
                .coerceIn(ALARM_ICON_MIN_SIZE_DP.dp, ICON_LANDSCAPE_MAX_SIZE_DP.dp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .fillMaxHeight()
            ) {
                /** Weights spread the icon and the button apart instead of a fixed spacer */
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(ICON_AREA_WEIGHT)
                ) {
                    AlarmPulsingIcon(
                        pulseScale = pulseScale,
                        iconSize = iconSize,
                        rootCoordinates = rootCoordinates,
                        onIconCenterInRootChanged = onIconCenterInRootChanged,
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(DISMISS_AREA_WEIGHT)
                ) {
                    AlarmDismissButton(
                        pulseScale = pulseScale,
                        onDismiss = onDismiss,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Alarm landscape — small phone",
    widthDp = 640,
    heightDp = 320,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmLandscapeContentSmallPhonePreview() {
    VartovyiTheme {
        AlarmLandscapeContent(
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

@Preview(
    name = "Alarm landscape — tablet",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun AlarmLandscapeContentTabletPreview() {
    VartovyiTheme {
        AlarmLandscapeContent(
            sourceChannelName = "Повітряна тривога • Харківська область",
            sourceMessageText = "Увага! Оголошено повітряну тривогу по всій території області. " +
                    "Зафіксовано зліт ворожої авіації та пуски крилатих ракет у напрямку " +
                    "регіону. " +
                    "Негайно пройдіть до найближчого укриття та залишайтеся там до відбою.",
            pulseScale = 1.05f,
            rootCoordinates = { null },
            onDismiss = {},
            onIconCenterInRootChanged = {},
        )
    }
}
