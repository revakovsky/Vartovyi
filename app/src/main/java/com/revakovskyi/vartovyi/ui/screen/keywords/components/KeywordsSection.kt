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
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage

@Composable
fun KeywordsSection(
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester,
    keywords: List<TriggerKeywordRule>,
    selectedTriggerKeywordRuleType: TriggerKeywordRuleType,
    inputValue: String,
    inputHint: String,
    onTypeSelected: (type: TriggerKeywordRuleType) -> Unit,
    onInputChange: (value: String) -> Unit,
    onAdd: () -> Unit,
    onCopy: (text: String) -> Unit,
    onRemove: (keyword: TriggerKeywordRule) -> Unit,
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

            TriggerRuleTypeSelector(
                selectedTriggerKeywordRuleType = selectedTriggerKeywordRuleType,
                onTypeSelected = onTypeSelected,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
            ) {
                WordInputRow(
                    value = inputValue,
                    hint = inputHint,
                    onClear = { onInputChange("") },
                    onValueChange = onInputChange,
                    onAdd = onAdd,
                    onFocusChanged = onFocusChanged,
                )

                if (keywords.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                    ) {
                        keywords.forEach { keywordRule ->
                            val modeLabel = getModeLabel(type = keywordRule.type)
                            val chipText = "[$modeLabel] ${keywordRule.displayValue}"
                            WordChip(
                                text = chipText,
                                containerColor = VartovyiTheme.colors.primaryContainer,
                                contentColor = VartovyiTheme.colors.onPrimaryContainer,
                                onLongPress = { onCopy(keywordRule.displayValue) },
                                onRemove = { onRemove(keywordRule) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getModeLabel(type: TriggerKeywordRuleType): String {
    return when (type) {
        TriggerKeywordRuleType.WORD -> stringResource(R.string.keywords_rule_type_word)
        TriggerKeywordRuleType.ALL_WORDS -> stringResource(R.string.keywords_rule_type_all_words)
        TriggerKeywordRuleType.PHRASE -> stringResource(R.string.keywords_rule_type_phrase)
    }
}

@Preview(name = "Keywords section — empty")
@Composable
private fun PreviewKeywordsSectionEmpty() {
    VartovyiTheme {
        KeywordsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            selectedTriggerKeywordRuleType = TriggerKeywordRuleType.WORD,
            keywords = emptyList(),
            inputValue = "",
            inputHint = "e.g. Saltivka",
            onTypeSelected = {},
            onInputChange = {},
            onAdd = {},
            onCopy = {},
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
            selectedTriggerKeywordRuleType = TriggerKeywordRuleType.ALL_WORDS,
            keywords = listOf(
                parseTriggerKeywordRuleFromStorage("Салтівка"),
                parseTriggerKeywordRuleFromStorage("ракета + харків"),
                parseTriggerKeywordRuleFromStorage("\"шахед на місто\""),
            ),
            inputValue = "",
            inputHint = "e.g. rocket + kharkiv",
            onTypeSelected = {},
            onInputChange = {},
            onAdd = {},
            onCopy = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}
