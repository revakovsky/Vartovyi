package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
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
fun OnboardingPageLayout(
    modifier: Modifier = Modifier,
    visual: OnboardingVisual,
    title: String,
    body: String = "",
    bodyContent: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = VartovyiTheme.spacing.medium)
    ) {
        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.massive))

        when (visual) {
            is OnboardingVisual.VectorIcon -> {
                Icon(
                    imageVector = visual.imageVector,
                    contentDescription = null,
                    tint = visual.tint,
                    modifier = Modifier.size(ONBOARDING_ICON_SIZE.dp)
                )
            }

            is OnboardingVisual.RasterImage -> {
                Image(
                    painter = painterResource(visual.resId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ONBOARDING_ICON_SIZE.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

        Text(
            text = title,
            style = VartovyiTheme.typography.headlineSmall,
            color = VartovyiTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        if (bodyContent != null) {
            bodyContent()
        } else {
            Text(
                text = body,
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (actionContent != null) {
            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            actionContent()
        }

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.large))
    }
}

@Preview(backgroundColor = 0xFF181717, showBackground = true)
@Composable
private fun OnboardingPageLayoutVectorPreview() {
    VartovyiTheme {
        OnboardingPageLayout(
            visual = OnboardingVisual.VectorIcon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                tint = VartovyiTheme.colors.primary,
            ),
            title = "Ласкаво просимо до Vartovyi",
            body = "Додаток стежить за Telegram у фоні та подає сирену, коли у повідомленні є ваші ключові слова.",
            modifier = Modifier.background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(backgroundColor = 0xFF181717, showBackground = true)
@Composable
private fun OnboardingPageLayoutRasterPreview() {
    VartovyiTheme {
        OnboardingPageLayout(
            visual = OnboardingVisual.RasterImage(resId = R.drawable.just_logo),
            title = "Ласкаво просимо до Vartovyi",
            body = "Додаток стежить за Telegram у фоні та подає сирену, коли у повідомленні є ваші ключові слова.",
            modifier = Modifier.background(VartovyiTheme.colors.background)
        )
    }
}
