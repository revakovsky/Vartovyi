package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun HomeKeywordChip(
    modifier: Modifier = Modifier,
    text: String,
) {
    Surface(
        color = VartovyiTheme.colors.primaryContainer,
        shape = VartovyiTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = VartovyiTheme.typography.labelMedium,
            color = VartovyiTheme.colors.onPrimaryContainer,
            modifier = Modifier.padding(
                horizontal = VartovyiTheme.spacing.small,
                vertical = VartovyiTheme.spacing.extraSmall,
            )
        )
    }
}

@Preview
@Composable
private fun PreviewHomeKeywordChip() {
    VartovyiTheme {
        HomeKeywordChip(
            text = "Test keyword"
        )
    }
}
