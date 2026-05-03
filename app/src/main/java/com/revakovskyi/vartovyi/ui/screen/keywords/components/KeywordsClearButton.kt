package com.revakovskyi.vartovyi.ui.screen.keywords.components

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
        borderColor = VartovyiTheme.colors.primary,
        icon = ImageVector.vectorResource(R.drawable.delete),
        modifier = modifier
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
