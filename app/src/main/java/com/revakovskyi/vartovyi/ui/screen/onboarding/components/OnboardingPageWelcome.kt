package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPageWelcome(
    modifier: Modifier = Modifier,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.RasterImage(resId = R.drawable.just_logo),
        title = stringResource(R.string.onboarding_welcome_title),
        bodyContent = { WelcomeBodyContent() },
        modifier = modifier,
    )
}

@Composable
private fun WelcomeBodyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.onboarding_welcome_tagline),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        PrivacyBadge()

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = stringResource(R.string.onboarding_welcome_explanation),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = stringResource(R.string.onboarding_welcome_siren),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = stringResource(R.string.onboarding_welcome_telegram_required),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = stringResource(R.string.onboarding_welcome_keywords_hint),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PrivacyBadge(modifier: Modifier = Modifier) {
    Surface(
        color = VartovyiTheme.colors.primaryContainer,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.onboarding_welcome_privacy_note),
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(VartovyiTheme.spacing.standard)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageWelcomePreview() {
    VartovyiTheme {
        OnboardingPageWelcome(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
