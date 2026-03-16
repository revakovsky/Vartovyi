package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val MAX_VISIBLE_KEYWORDS = 5

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordsCard(
    keywords: List<String>,
    onMoreClick: () -> Unit,
    onAddKeywords: () -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VartovyiTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.home_keywords_title),
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

                TextButton(onClick = onAddKeywords) {
                    Text(
                        text = stringResource(R.string.home_add),
                        color = VartovyiTheme.colors.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else {
                val visibleKeywords = keywords.take(MAX_VISIBLE_KEYWORDS)
                val remainingCount = keywords.size - visibleKeywords.size

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                ) {
                    visibleKeywords.forEach { keyword ->
                        HomeKeywordChip(text = keyword)
                    }

                    if (remainingCount > 0) {
                        HomeKeywordChip(
                            text = stringResource(R.string.home_and_more, remainingCount),
                            onClick = onMoreClick,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Keywords card — short words")
@Composable
private fun PreviewKeywordsCard() {
    VartovyiTheme {
        KeywordsCard(
            keywords = listOf("ракета", "вибух", "тривога", "атака", "бомба"),
            onAddKeywords = {},
            onMoreClick = {},
        )
    }
}

@Preview(name = "Keywords card — empty")
@Composable
private fun PreviewKeywordsCardEmpty() {
    VartovyiTheme {
        KeywordsCard(
            keywords = listOf(),
            onAddKeywords = {},
            onMoreClick = {},
        )
    }
}

@Preview(name = "Keywords card — long words")
@Composable
private fun PreviewKeywordsCardLongWords() {
    VartovyiTheme {
        KeywordsCard(
            keywords = listOf(
                "Салтівка",
                "sdffds lgfsgkld lfdskgjdsgj ldfgldjgjsdfogjs odfgj lfdskgjdsgj",
                "вибух",
                "короткий текст та ще трохи довший текст",
            ),
            onAddKeywords = {},
            onMoreClick = {},
        )
    }
}
