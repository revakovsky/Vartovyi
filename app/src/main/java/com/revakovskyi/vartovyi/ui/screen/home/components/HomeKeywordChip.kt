package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun HomeKeywordChip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        color = VartovyiTheme.colors.primaryContainer,
        shape = VartovyiTheme.shapes.small,
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
    ) {
        Text(
            text = text,
            style = VartovyiTheme.typography.labelMedium,
            color = VartovyiTheme.colors.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = VartovyiTheme.spacing.small,
                vertical = VartovyiTheme.spacing.extraSmall,
            ),
        )
    }
}

@Preview(name = "Home keyword chip")
@Composable
private fun PreviewHomeKeywordChip() {
    VartovyiTheme {
        HomeKeywordChip(text = "Test keyword")
    }
}

@Preview(name = "Home keyword chip — long text")
@Composable
private fun PreviewHomeKeywordChipLong() {
    VartovyiTheme {
        HomeKeywordChip(text = "sdffds lgfsgkld lfdskgjdsgj ldfgldjgjsdfogjs odfgj")
    }
}
