package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val SCREENSHOT_WIDTH_FRACTION = 0.9f

@Composable
fun OnboardingPageTelegram(
    modifier: Modifier = Modifier,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.telegram),
            tint = Color.Unspecified,
        ),
        title = stringResource(R.string.onboarding_telegram_title),
        bodyContent = { TelegramBodyContent() },
        modifier = modifier
    )
}

@Composable
private fun TelegramBodyContent(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraLarge),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.onboarding_telegram_required),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.onboarding_telegram_intro),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        TelegramStep(
            title = stringResource(R.string.onboarding_telegram_step_1_title),
            body = stringResource(R.string.onboarding_telegram_step_1_body),
        )

        TelegramScreenshot(resId = R.drawable.tg_1)

        TelegramStep(
            title = stringResource(R.string.onboarding_telegram_step_2_title),
            body = stringResource(R.string.onboarding_telegram_step_2_body),
        )

        TelegramScreenshot(resId = R.drawable.tg_2)

        TelegramStep(
            title = stringResource(R.string.onboarding_telegram_step_3_title),
            body = stringResource(R.string.onboarding_telegram_step_3_body),
        )

        TelegramScreenshot(resId = R.drawable.tg_3)

        TelegramScreenshot(resId = R.drawable.tg_4)
    }
}

@Composable
private fun TelegramStep(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = VartovyiTheme.typography.titleMedium,
            color = VartovyiTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = body,
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TelegramScreenshot(
    modifier: Modifier = Modifier,
    @DrawableRes resId: Int,
) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier.fillMaxWidth(SCREENSHOT_WIDTH_FRACTION)
    )
}

@Preview(showBackground = true, device = "spec:width=1080px,height=3500px,dpi=440")
@Composable
private fun OnboardingPageTelegramPreview() {
    VartovyiTheme {
        OnboardingPageTelegram(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(
    name = "Onboarding Telegram — landscape",
    widthDp = 800,
    heightDp = 360,
    showBackground = true,
)
@Composable
private fun OnboardingPageTelegramLandscapePreview() {
    VartovyiTheme {
        OnboardingPageTelegram(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
