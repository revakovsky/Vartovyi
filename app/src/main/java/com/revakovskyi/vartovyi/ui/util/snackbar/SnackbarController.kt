package com.revakovskyi.vartovyi.ui.util.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val messageMaxLines: Int = 3,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit,
)
