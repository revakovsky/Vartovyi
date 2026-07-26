package com.revakovskyi.vartovyi.ui.alarm.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.alarm.utils.Constants.ALARM_ICON_MAX_SIZE_DP
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

/** Reports its center so the animated background can pulse rings from the same point */
@Composable
internal fun AlarmPulsingIcon(
    modifier: Modifier = Modifier,
    pulseScale: Float,
    iconSize: Dp = ALARM_ICON_MAX_SIZE_DP.dp,
    rootCoordinates: () -> LayoutCoordinates?,
    onIconCenterInRootChanged: (center: Offset) -> Unit,
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.alarm),
        contentDescription = null,
        tint = VartovyiTheme.colors.error,
        modifier = modifier
            .size(iconSize)
            .onGloballyPositioned { iconCoordinates ->
                val currentRootCoordinates = rootCoordinates()
                if (
                    currentRootCoordinates == null ||
                    !currentRootCoordinates.isAttached ||
                    !iconCoordinates.isAttached
                ) {
                    return@onGloballyPositioned
                }

                val topLeftInRoot = currentRootCoordinates.localPositionOf(
                    sourceCoordinates = iconCoordinates,
                    relativeToSource = Offset.Zero,
                )
                val iconCenterInRoot = topLeftInRoot + Offset(
                    x = iconCoordinates.size.width / 2f,
                    y = iconCoordinates.size.height / 2f,
                )

                onIconCenterInRootChanged(iconCenterInRoot)
            }
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
    )
}

@Preview(name = "Alarm pulsing icon", showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun AlarmPulsingIconPreview() {
    VartovyiTheme {
        AlarmPulsingIcon(
            pulseScale = 1.05f,
            rootCoordinates = { null },
            onIconCenterInRootChanged = {},
        )
    }
}
