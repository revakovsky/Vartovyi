package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun TriggerRuleTypeSelector(
    selectedTriggerKeywordRuleType: TriggerKeywordRuleType,
    onTypeSelected: (type: TriggerKeywordRuleType) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
    ) {
        TriggerRuleTypeChip(
            title = stringResource(R.string.keywords_rule_type_word),
            isSelected = selectedTriggerKeywordRuleType == TriggerKeywordRuleType.WORD,
            onClick = { onTypeSelected(TriggerKeywordRuleType.WORD) },
        )

        TriggerRuleTypeChip(
            title = stringResource(R.string.keywords_rule_type_all_words),
            isSelected = selectedTriggerKeywordRuleType == TriggerKeywordRuleType.ALL_WORDS,
            onClick = { onTypeSelected(TriggerKeywordRuleType.ALL_WORDS) },
        )

        TriggerRuleTypeChip(
            title = stringResource(R.string.keywords_rule_type_phrase),
            isSelected = selectedTriggerKeywordRuleType == TriggerKeywordRuleType.PHRASE,
            onClick = { onTypeSelected(TriggerKeywordRuleType.PHRASE) },
        )
    }
}

@Composable
private fun TriggerRuleTypeChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val selectedContainerColor: Color = VartovyiTheme.colors.primaryContainer
    val selectedContentColor: Color = VartovyiTheme.colors.onPrimaryContainer
    val defaultContainerColor: Color = VartovyiTheme.colors.surfaceVariant
    val defaultContentColor: Color = VartovyiTheme.colors.onSurfaceVariant

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            androidx.compose.material3.Text(
                text = title,
                style = VartovyiTheme.typography.bodyMedium,
            )
        },
        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedContainerColor,
            selectedLabelColor = selectedContentColor,
            containerColor = defaultContainerColor,
            labelColor = defaultContentColor,
        ),
    )
}

@Preview
@Composable
private fun PreviewTriggerRuleTypeSelector() {
    VartovyiTheme {
        TriggerRuleTypeSelector(
            selectedTriggerKeywordRuleType = TriggerKeywordRuleType.WORD,
            onTypeSelected = { _ -> }
        )
    }
}
