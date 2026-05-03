package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.ScrollProgressBar
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ONBOARDING_TELEGRAM_ICON_SIZE = 100
private const val SCREENSHOT_WIDTH_FRACTION = 0.85f

@Composable
fun OnboardingPageTelegram(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = VartovyiTheme.spacing.medium),
        ) {
            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.massive))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.telegram),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(ONBOARDING_TELEGRAM_ICON_SIZE.dp)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            Text(
                text = stringResource(R.string.onboarding_telegram_title),
                style = VartovyiTheme.typography.headlineSmall,
                color = VartovyiTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            Text(
                text = stringResource(R.string.onboarding_telegram_body_1),
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            Image(
                painter = painterResource(R.drawable.tg_1),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(SCREENSHOT_WIDTH_FRACTION)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_telegram_body_2),
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            Image(
                painter = painterResource(R.drawable.tg_2),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(SCREENSHOT_WIDTH_FRACTION)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            Image(
                painter = painterResource(R.drawable.tg_4),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(SCREENSHOT_WIDTH_FRACTION)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraLarge))

            Image(
                painter = painterResource(R.drawable.tg_3),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(SCREENSHOT_WIDTH_FRACTION)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.large))
        }

        ScrollProgressBar(
            scrollState = scrollState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
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
