package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val DISABLED_ICON_ALPHA = 0.38f
private const val INFO_ICON_BACKGROUND_ALPHA = 0.35f

@Composable
fun LogTopBarActions(
    modifier: Modifier = Modifier,
    isClearEnabled: Boolean,
    onInfoClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClearClick,
            enabled = isClearEnabled,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.delete),
                contentDescription = stringResource(R.string.log_clear),
                tint =
                    if (isClearEnabled) VartovyiTheme.colors.primary
                    else VartovyiTheme.colors.onSurfaceVariant.copy(alpha = DISABLED_ICON_ALPHA),
                modifier = Modifier.size(VartovyiTheme.spacing.extraLarge)
            )
        }

        IconButton(onClick = onInfoClick) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(VartovyiTheme.spacing.extraSmall)
                    .background(
                        shape = CircleShape,
                        color = VartovyiTheme.colors.onSurfaceVariant
                            .copy(alpha = INFO_ICON_BACKGROUND_ALPHA),
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    contentDescription = null,
                    modifier = Modifier.size(VartovyiTheme.spacing.standard)
                )
            }
        }
    }
}

@Preview(name = "Log top bar actions — clear enabled")
@Composable
private fun LogTopBarActionsEnabledPreview() {
    VartovyiTheme {
        LogTopBarActions(
            isClearEnabled = true,
            onInfoClick = {},
            onClearClick = {},
        )
    }
}

@Preview(name = "Log top bar actions — clear disabled")
@Composable
private fun LogTopBarActionsDisabledPreview() {
    VartovyiTheme {
        LogTopBarActions(
            isClearEnabled = false,
            onInfoClick = {},
            onClearClick = {},
        )
    }
}
