package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun KeywordsSection(
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester,
    keywords: List<String>,
    inputValue: String,
    onInputChange: (value: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (keyword: String) -> Unit,
    onFocusChanged: (isFocused: Boolean) -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        ) {
            SectionTitle(
                title = stringResource(R.string.keywords_trigger_words),
                tooltipText = stringResource(R.string.keywords_trigger_tooltip),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
            ) {
                WordInputRow(
                    value = inputValue,
                    hint = stringResource(R.string.keywords_trigger_hint),
                    onValueChange = onInputChange,
                    onAdd = onAdd,
                    onFocusChanged = onFocusChanged,
                )

                if (keywords.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                    ) {
                        keywords.forEach { keyword ->
                            WordChip(
                                text = keyword,
                                containerColor = VartovyiTheme.colors.primaryContainer,
                                contentColor = VartovyiTheme.colors.onPrimaryContainer,
                                onRemove = { onRemove(keyword) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Keywords section — empty")
@Composable
private fun PreviewKeywordsSectionEmpty() {
    VartovyiTheme {
        KeywordsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            keywords = emptyList(),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}

@Preview(name = "Keywords section — with words")
@Composable
private fun PreviewKeywordsSectionWithWords() {
    VartovyiTheme {
        KeywordsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            keywords = listOf("Салтівка", "ракета", "вибух", "тривога", "атака"),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}
