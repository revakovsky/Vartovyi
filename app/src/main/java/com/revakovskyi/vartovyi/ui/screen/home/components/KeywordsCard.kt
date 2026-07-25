package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.ui.components.VartovyiSurface
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.theme.bodyLink

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordsCard(
    keywords: List<String>,
    onClick: () -> Unit,
) {
    VartovyiSurface(
        isClickable = true,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VartovyiTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.nav_keywords),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

            if (keywords.isEmpty()) {
                Text(
                    text = stringResource(R.string.home_add_keywords_hint),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )

                TextButton(onClick = onClick) {
                    Text(
                        text = stringResource(R.string.home_add),
                        style = VartovyiTheme.typography.bodyLink,
                        color = VartovyiTheme.colors.primary,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.home_replace_default_keywords_hint),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                ) {
                    keywords.forEach { keyword ->
                        HomeKeywordChip(text = keyword)
                    }
                }
            }
        }
    }
}

@Preview(name = "Keywords card — empty")
@Composable
private fun PreviewKeywordsCardEmpty() {
    VartovyiTheme {
        KeywordsCard(
            keywords = emptyList(),
            onClick = {},
        )
    }
}

@Preview(name = "Keywords card — default keywords only")
@Composable
private fun PreviewKeywordsCardDefaultOnly() {
    VartovyiTheme {
        KeywordsCard(
            keywords = DEFAULT_KEYWORDS_SEED,
            onClick = {},
        )
    }
}
