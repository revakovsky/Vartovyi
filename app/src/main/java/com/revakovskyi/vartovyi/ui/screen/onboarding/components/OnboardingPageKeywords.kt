package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
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
        body = stringResource(R.string.onboarding_keywords_body),
        actionContent = {
            VartovyiActionButton(
                text = stringResource(R.string.onboarding_open_keywords),
                onClick = onOpenKeywords,
                style = VartovyiActionButtonStyle.Outlined,
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

            Text(
                text = stringResource(R.string.onboarding_keywords_later_hint),
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
