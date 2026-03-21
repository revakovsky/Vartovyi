package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun LogErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = VartovyiTheme.spacing.small)
    ) {
        Text(
            text = stringResource(R.string.log_load_failed),
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

        VartovyiActionButton(
            text = stringResource(R.string.log_retry),
            onClick = onRetry,
            style = VartovyiActionButtonStyle.Filled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Log error state")
@Composable
private fun LogErrorStatePreview() {
    VartovyiTheme {
        LogErrorState(
            onRetry = {},
        )
    }
}
