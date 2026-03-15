package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val TEST_BUTTON_MAX_WIDTH_DP = 450
private const val BORDER_STROKE_WIDTH_DP = 1

@Composable
fun LogClearButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        border = BorderStroke(
            width = BORDER_STROKE_WIDTH_DP.dp,
            color = if (isEnabled) VartovyiTheme.colors.primary else VartovyiTheme.colors.onSurfaceVariant,
        ),
        modifier = modifier
            .padding(vertical = VartovyiTheme.spacing.standard)
            .widthIn(max = TEST_BUTTON_MAX_WIDTH_DP.dp)
            .fillMaxWidth()
            .height(VartovyiTheme.spacing.massive)
    ) {
        Text(
            text = stringResource(R.string.log_clear),
            style = VartovyiTheme.typography.titleMedium,
            color = if (isEnabled) VartovyiTheme.colors.onPrimary else VartovyiTheme.colors.onSurfaceVariant,
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
