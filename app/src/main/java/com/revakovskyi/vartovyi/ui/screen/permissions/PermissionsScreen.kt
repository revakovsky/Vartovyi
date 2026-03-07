package com.revakovskyi.vartovyi.ui.screen.permissions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = koinActivityViewModel(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsState()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is PermissionsUiContract.Event.AllPermissionsGranted -> onNavigateBack()
            is PermissionsUiContract.Event.NavigateBack -> onNavigateBack()
            is PermissionsUiContract.Event.NavigateToSystemSettings -> {
                val intent = Intent(event.action).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
    }

    PermissionsContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun PermissionsContent(
    state: PermissionsUiContract.State,
    onAction: (action: PermissionsUiContract.Action) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Permissions — all granted: ${state.allGranted}",
            color = VartovyiTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun PermissionsContentPreview() {
    VartovyiTheme {
        PermissionsContent(
            state = PermissionsUiContract.State(),
            onAction = {},
        )
    }
}
