package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun LogClearButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.log_clear),
            style = VartovyiTheme.typography.labelLarge,
        )
    }
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
