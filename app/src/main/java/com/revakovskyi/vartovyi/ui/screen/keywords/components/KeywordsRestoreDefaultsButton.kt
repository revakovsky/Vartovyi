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
fun KeywordsRestoreDefaultsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    VartovyiActionButton(
        text = stringResource(R.string.keywords_restore_defaults),
        onClick = onClick,
        style = VartovyiActionButtonStyle.Outlined,
        borderColor = VartovyiTheme.colors.primary,
        icon = ImageVector.vectorResource(R.drawable.restore),
        modifier = modifier
    )
}

@Preview(name = "Keywords restore defaults")
@Composable
private fun KeywordsRestoreDefaultsButtonPreview() {
    VartovyiTheme {
        KeywordsRestoreDefaultsButton(
            onClick = {},
        )
    }
}
