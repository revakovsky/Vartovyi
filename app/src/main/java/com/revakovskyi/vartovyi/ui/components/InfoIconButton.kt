package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ICON_SIZE = 16
private const val INFO_ICON_BACKGROUND_ALPHA = 0.35f

@Composable
fun InfoIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(
                alpha = INFO_ICON_BACKGROUND_ALPHA
            ),
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.info),
            contentDescription = null,
            tint = VartovyiTheme.colors.onBackground,
            modifier = Modifier.size(ICON_SIZE.dp),
        )
    }
}

@Preview(name = "Info icon button")
@Composable
private fun PreviewInfoIconButton() {
    VartovyiTheme {
        InfoIconButton(onClick = {})
    }
}
