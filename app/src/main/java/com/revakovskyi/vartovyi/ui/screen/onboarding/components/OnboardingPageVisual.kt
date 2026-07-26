package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ONBOARDING_ICON_SIZE = 100

sealed interface OnboardingVisual {

    data class VectorIcon(
        val imageVector: ImageVector,
        val tint: Color,
    ) : OnboardingVisual

    data class RasterImage(
        @param:DrawableRes val resId: Int,
    ) : OnboardingVisual

}

@Composable
internal fun OnboardingPageVisual(
    modifier: Modifier = Modifier,
    visual: OnboardingVisual,
) {
    when (visual) {
        is OnboardingVisual.VectorIcon -> {
            Icon(
                imageVector = visual.imageVector,
                contentDescription = null,
                tint = visual.tint,
                modifier = modifier.size(ONBOARDING_ICON_SIZE.dp)
            )
        }

        is OnboardingVisual.RasterImage -> {
            Image(
                painter = painterResource(visual.resId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier
                    .fillMaxWidth()
                    .height(ONBOARDING_ICON_SIZE.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF181717)
@Composable
private fun OnboardingPageVisualPreview() {
    VartovyiTheme {
        OnboardingPageVisual(
            visual = OnboardingVisual.VectorIcon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                tint = VartovyiTheme.colors.primary,
            ),
            modifier = Modifier.background(VartovyiTheme.colors.background)
        )
    }
}
