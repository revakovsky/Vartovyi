package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val MENU_MIN_WIDTH_DP = 200

@Composable
fun KeywordsTopBarActionsIcon(
    modifier: Modifier = Modifier,
    isExportEnabled: Boolean,
    isClearEnabled: Boolean,
    initiallyExpanded: Boolean = false,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(initiallyExpanded) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { isMenuExpanded = true },
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                contentDescription = stringResource(R.string.keywords_menu_content_description),
                tint = VartovyiTheme.colors.onBackground,
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            containerColor = VartovyiTheme.colors.background,
            modifier = Modifier.widthIn(min = MENU_MIN_WIDTH_DP.dp),
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.keywords_export)) },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.export),
                        contentDescription = null,
                        modifier = Modifier.size(VartovyiTheme.spacing.large),
                    )
                },
                enabled = isExportEnabled,
                onClick = {
                    isMenuExpanded = false
                    onExportClick()
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.keywords_import)) },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.resource_import),
                        contentDescription = null,
                        modifier = Modifier.size(VartovyiTheme.spacing.large),
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onImportClick()
                },
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.keywords_clear),
                        color = VartovyiTheme.colors.error,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = null,
                        tint = VartovyiTheme.colors.error,
                        modifier = Modifier.size(VartovyiTheme.spacing.standard),
                    )
                },
                enabled = isClearEnabled,
                onClick = {
                    isMenuExpanded = false
                    onClearClick()
                },
            )
        }
    }
}

@Preview(name = "Keywords top bar actions — menu expanded")
@Composable
private fun KeywordsTopBarActionsIconExpandedPreview() {
    VartovyiTheme {
        KeywordsTopBarActionsIcon(
            isExportEnabled = true,
            isClearEnabled = true,
            initiallyExpanded = true,
            onExportClick = {},
            onImportClick = {},
            onClearClick = {},
        )
    }
}

@Preview(name = "Keywords top bar actions — export/clear disabled")
@Composable
private fun KeywordsTopBarActionsIconDisabledPreview() {
    VartovyiTheme {
        KeywordsTopBarActionsIcon(
            isExportEnabled = false,
            isClearEnabled = false,
            initiallyExpanded = true,
            onExportClick = {},
            onImportClick = {},
            onClearClick = {},
        )
    }
}
