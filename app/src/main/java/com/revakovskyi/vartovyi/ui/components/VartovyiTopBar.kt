package com.revakovskyi.vartovyi.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VartovyiTopBar(
    modifier: Modifier = Modifier,
    title: String,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = VartovyiTheme.typography.titleLarge,
                color = VartovyiTheme.colors.onBackground,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VartovyiTheme.colors.background,
        ),
        modifier = modifier
    )
}

@Preview
@Composable
private fun VartovyiTopBarPreview() {
    VartovyiTheme {
        VartovyiTopBar(title = "Вартовий")
    }
}
