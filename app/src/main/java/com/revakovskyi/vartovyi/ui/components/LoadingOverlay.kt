package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ICON_SIZE_DP = 88

@Composable
fun LoadingOverlay() {
    val signalsFrame = rememberMonitoringActiveIconSignalsFrame(
        infiniteTransitionLabel = "loading_transition",
    )
    val ringColor = VartovyiTheme.colors.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMonitoringActiveIconSignalRings(
                frame = signalsFrame,
                ringColor = ringColor,
                strokeWidthPx = MONITORING_ACTIVE_SIGNAL_RING_STROKE_WIDTH_DP.dp.toPx(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                contentDescription = null,
                tint = VartovyiTheme.colors.primary,
                modifier = Modifier
                    .size(ICON_SIZE_DP.dp)
                    .graphicsLayer {
                        scaleX = signalsFrame.iconScale
                        scaleY = signalsFrame.iconScale
                    }
            )

            Text(
                text = stringResource(R.string.app_name),
                style = VartovyiTheme.typography.headlineSmall,
                color = VartovyiTheme.colors.onSurface,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLoadingOverlay() {
    VartovyiTheme {
        LoadingOverlay()
    }
}
