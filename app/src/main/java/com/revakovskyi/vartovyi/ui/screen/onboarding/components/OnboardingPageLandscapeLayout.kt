package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.ScrollProgressBar
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val VISUAL_PANE_WEIGHT = 2f
private const val TEXT_PANE_WEIGHT = 8f

@Composable
internal fun OnboardingPageLandscapeLayout(
    modifier: Modifier = Modifier,
    visual: OnboardingVisual,
    title: String,
    body: String = "",
    bodyContent: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
) {
    val scrollState = rememberScrollState()

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
            modifier = Modifier
                .displayCutoutPadding()
                .fillMaxSize()
                .padding(horizontal = VartovyiTheme.spacing.medium)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(VISUAL_PANE_WEIGHT)
                    .fillMaxSize()
            ) {
                OnboardingPageVisual(visual = visual)
            }

            BoxWithConstraints(modifier = Modifier.weight(TEXT_PANE_WEIGHT)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .heightIn(min = maxHeight)
                        .padding(vertical = VartovyiTheme.spacing.standard)
                ) {
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
                }

                ScrollProgressBar(
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Preview(
    name = "Onboarding landscape — small phone",
    widthDp = 640,
    heightDp = 320,
    showBackground = true,
)
@Composable
private fun OnboardingPageLandscapeLayoutSmallPhonePreview() {
    VartovyiTheme {
        OnboardingPageLandscapeLayout(
            visual = OnboardingVisual.VectorIcon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                tint = VartovyiTheme.colors.primary,
            ),
            title = "Ласкаво просимо до Vartovyi",
            body = "Додаток стежить за Telegram у фоні та подає сирену, коли у повідомленні " +
                    "є ваші ключові слова.",
            modifier = Modifier.background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(
    name = "Onboarding landscape — tablet",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true,
)
@Composable
private fun OnboardingPageLandscapeLayoutTabletPreview() {
    VartovyiTheme {
        OnboardingPageLandscapeLayout(
            visual = OnboardingVisual.VectorIcon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                tint = VartovyiTheme.colors.primary,
            ),
            title = "Ласкаво просимо до Vartovyi",
            body = "Додаток стежить за Telegram у фоні та подає сирену, коли у повідомленні " +
                    "є ваші ключові слова.",
            modifier = Modifier.background(VartovyiTheme.colors.background)
        )
    }
}
