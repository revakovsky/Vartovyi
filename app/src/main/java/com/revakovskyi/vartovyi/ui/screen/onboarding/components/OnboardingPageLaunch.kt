package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPageLaunch(
    modifier: Modifier = Modifier,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.security_on),
            tint = VartovyiTheme.colors.primary,
        ),
        title = stringResource(R.string.onboarding_launch_title),
        body = stringResource(R.string.onboarding_launch_body),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageLaunchPreview() {
    VartovyiTheme {
        OnboardingPageLaunch(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
