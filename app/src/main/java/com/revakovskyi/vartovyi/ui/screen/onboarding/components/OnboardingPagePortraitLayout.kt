package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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

@Composable
internal fun OnboardingPagePortraitLayout(
    modifier: Modifier = Modifier,
    visual: OnboardingVisual,
    title: String,
    body: String = "",
    bodyContent: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .padding(horizontal = VartovyiTheme.spacing.medium)
            ) {
                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.massive))

                OnboardingPageVisual(visual = visual)

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

        ScrollProgressBar(
            scrollState = scrollState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(name = "Onboarding portrait", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun OnboardingPagePortraitLayoutPreview() {
    VartovyiTheme {
        OnboardingPagePortraitLayout(
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
