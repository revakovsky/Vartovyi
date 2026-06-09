package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPageKeywords(
    modifier: Modifier = Modifier,
    onOpenKeywords: () -> Unit,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.words),
            tint = VartovyiTheme.colors.primary,
        ),
        title = stringResource(R.string.onboarding_keywords_title),
        bodyContent = { KeywordsBodyContent() },
        actionContent = {
            Text(
                text = stringResource(R.string.onboarding_keywords_later_hint),
                style = VartovyiTheme.typography.bodySmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

            VartovyiActionButton(
                text = stringResource(R.string.onboarding_open_keywords),
                onClick = onOpenKeywords,
                style = VartovyiActionButtonStyle.Outlined,
                borderColor = VartovyiTheme.colors.primary,
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun KeywordsBodyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.onboarding_keywords_intro),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        KeywordsSectionItem(
            header = stringResource(R.string.onboarding_keywords_trigger_words_header),
            body = stringResource(R.string.onboarding_keywords_trigger_words_body),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        KeywordsSectionItem(
            header = stringResource(R.string.onboarding_keywords_stop_words_header),
            body = stringResource(R.string.onboarding_keywords_stop_words_body),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        KeywordsSectionItem(
            header = stringResource(R.string.onboarding_keywords_channel_filter_header),
            body = stringResource(R.string.onboarding_keywords_channel_filter_body),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = stringResource(R.string.onboarding_keywords_notes),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun KeywordsSectionItem(
    modifier: Modifier = Modifier,
    header: String,
    body: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = header,
            style = VartovyiTheme.typography.titleMedium,
            color = VartovyiTheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = body,
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageKeywordsPreview() {
    VartovyiTheme {
        OnboardingPageKeywords(
            onOpenKeywords = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
