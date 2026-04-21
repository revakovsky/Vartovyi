package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
        body = stringResource(R.string.onboarding_welcome_body),
        actionContent = {
            Text(
                text = stringResource(R.string.onboarding_welcome_privacy_note),
                style = VartovyiTheme.typography.bodySmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier,
    )

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
