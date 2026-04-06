package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

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
        borderColor = VartovyiTheme.colors.primary,
        icon = ImageVector.vectorResource(R.drawable.delete),
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
