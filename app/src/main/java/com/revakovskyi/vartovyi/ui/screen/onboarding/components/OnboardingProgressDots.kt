package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val DOT_SIZE = 8
private const val DOT_SIZE_ACTIVE = 10

@Composable
fun OnboardingProgressDots(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPages: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage

            Box(
                modifier = Modifier
                    .size(if (isActive) DOT_SIZE_ACTIVE.dp else DOT_SIZE.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) VartovyiTheme.colors.primary
                        else VartovyiTheme.colors.onSurfaceVariant
                    )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun OnboardingProgressDotsPreview() {
    VartovyiTheme {
        OnboardingProgressDots(
            currentPage = 1,
            totalPages = 5,
        )
    }
}
