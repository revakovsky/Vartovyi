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
fun StopWordsSection(
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester,
    stopWords: List<String>,
    inputValue: String,
    onInputChange: (value: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (stopWord: String) -> Unit,
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
                title = stringResource(R.string.keywords_stop_words),
                tooltipText = stringResource(R.string.keywords_stop_tooltip),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
            ) {
                WordInputRow(
                    value = inputValue,
                    hint = stringResource(R.string.keywords_stop_hint),
                    onValueChange = onInputChange,
                    onAdd = onAdd,
                    onFocusChanged = onFocusChanged,
                )

                if (stopWords.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                    ) {
                        stopWords.forEach { stopWord ->
                            WordChip(
                                text = stopWord,
                                containerColor = VartovyiTheme.colors.secondaryContainer,
                                contentColor = VartovyiTheme.colors.onSecondaryContainer,
                                onRemove = { onRemove(stopWord) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Stop words section — empty")
@Composable
private fun PreviewStopWordsSectionEmpty() {
    VartovyiTheme {
        StopWordsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            stopWords = emptyList(),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}

@Preview(name = "Stop words section — with words")
@Composable
private fun PreviewStopWordsSectionWithWords() {
    VartovyiTheme {
        StopWordsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            stopWords = listOf("відбій", "чисто", "минула"),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}
