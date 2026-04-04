package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun KeywordsClearButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    VartovyiActionButton(
        text = stringResource(R.string.keywords_clear),
        onClick = onClick,
        style = VartovyiActionButtonStyle.Outlined,
        enabled = isEnabled,
        contentColor = if (isEnabled) VartovyiTheme.colors.onPrimary else VartovyiTheme.colors.onSurfaceVariant,
        borderColor = if (isEnabled) VartovyiTheme.colors.primary else VartovyiTheme.colors.onSurfaceVariant,
        modifier = modifier.padding(vertical = VartovyiTheme.spacing.standard)
    )
}

@Preview(name = "Keywords clear — enabled")
@Composable
private fun KeywordsClearButtonEnabledPreview() {
    VartovyiTheme {
        KeywordsClearButton(
            isEnabled = true,
            onClick = {},
        )
    }
}

@Preview(name = "Keywords clear — disabled")
@Composable
private fun KeywordsClearButtonDisabledPreview() {
    VartovyiTheme {
        KeywordsClearButton(
            isEnabled = false,
            onClick = {},
        )
    }
}
