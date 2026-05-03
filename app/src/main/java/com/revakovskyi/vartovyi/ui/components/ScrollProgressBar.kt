package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val SCROLL_PROGRESS_BAR_HEIGHT_DP = 3
private const val SCROLL_PROGRESS_BAR_TRACK_ALPHA = 0.25f

@Composable
fun ScrollProgressBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
) {
    val isScrollable by remember { derivedStateOf { scrollState.maxValue in 1 until Int.MAX_VALUE } }

    val scrollProgress by remember {
        derivedStateOf {
            if (scrollState.maxValue == 0) 0f
            else scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        }
    }

    ScrollProgressBarContent(
        isVisible = isScrollable,
        progress = scrollProgress,
        modifier = modifier,
    )
}

@Composable
private fun ScrollProgressBarContent(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    progress: Float,
) {
    if (isVisible) {
        LinearProgressIndicator(
            progress = { progress },
            color = VartovyiTheme.colors.secondary,
            trackColor = VartovyiTheme.colors.secondary.copy(alpha = SCROLL_PROGRESS_BAR_TRACK_ALPHA),
            modifier = modifier
                .fillMaxWidth()
                .height(SCROLL_PROGRESS_BAR_HEIGHT_DP.dp)
        )
    }
}

@Preview(name = "50% scrolled", showBackground = true, backgroundColor = 0xFF181717)
@Composable
private fun ScrollProgressBarScrolledPreview() {
    VartovyiTheme {
        ScrollProgressBarContent(
            isVisible = true,
            progress = 0.5f,
        )
    }
}

@Preview(name = "Content fits — bar hidden", showBackground = true, backgroundColor = 0xFF181717)
@Composable
private fun ScrollProgressBarHiddenPreview() {
    VartovyiTheme {
        ScrollProgressBarContent(
            isVisible = true,
            progress = 0f,
        )
    }
}
