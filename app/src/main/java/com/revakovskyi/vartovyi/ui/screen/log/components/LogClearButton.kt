package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val TEST_BUTTON_MAX_WIDTH_DP = 450

@Composable
fun LogClearButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    VartovyiActionButton(
        text = stringResource(R.string.log_clear),
        onClick = onClick,
        style = VartovyiActionButtonStyle.Outlined,
        enabled = isEnabled,
        contentColor = if (isEnabled) VartovyiTheme.colors.onPrimary else VartovyiTheme.colors.onSurfaceVariant,
        borderColor = if (isEnabled) VartovyiTheme.colors.primary else VartovyiTheme.colors.onSurfaceVariant,
        maxWidth = TEST_BUTTON_MAX_WIDTH_DP.dp,
        modifier = modifier.padding(vertical = VartovyiTheme.spacing.standard)
    )
}

@Preview(name = "Log clear button - enabled")
@Composable
private fun LogClearButtonEnabledPreview() {
    VartovyiTheme {
        LogClearButton(
            isEnabled = true,
            onClick = {},
        )
    }
}

@Preview(name = "Log clear button - disabled")
@Composable
private fun LogClearButtonDisabledPreview() {
    VartovyiTheme {
        LogClearButton(
            isEnabled = false,
            onClick = {},
        )
    }
}
