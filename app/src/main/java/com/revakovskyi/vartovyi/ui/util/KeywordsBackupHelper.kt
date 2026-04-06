package com.revakovskyi.vartovyi.ui.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val EXPORT_FILENAME_PREFIX = "vartovyi_keywords_backup"
private const val EXPORT_FILENAME_EXTENSION = ".json"
private const val EXPORT_FILENAME_DATE_PATTERN = "yyyy-MM-dd_HH-mm-ss"
private const val TAG = "KeywordsBackupHelper"

@Stable
class KeywordsBackupHelper(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onAction: (action: KeywordsUiContract.Action) -> Unit,
) {

    internal lateinit var exportLauncher: ActivityResultLauncher<String>
    internal lateinit var importLauncher: ActivityResultLauncher<Array<String>>

    private var pendingExportContent: String? = null

    fun handleEvent(event: KeywordsUiContract.Event) {
        when (event) {
            is KeywordsUiContract.Event.LaunchExportFilePicker -> launchExportPicker(event.jsonContent)
            is KeywordsUiContract.Event.LaunchImportFilePicker -> launchImportPicker()
            else -> Unit
        }
    }

    internal fun handleExportResult(uri: Uri?) {
        uri ?: return
        val content = pendingExportContent ?: return

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(content.toByteArray(Charsets.UTF_8))
                    }
                }
                pendingExportContent = null
                onAction(KeywordsUiContract.Action.NotifyExportSuccess)
            } catch (e: Exception) {
                Log.e(TAG, "handleExportResult: exception", e)
                pendingExportContent = null
                onAction(KeywordsUiContract.Action.NotifyExportError)
            }
        }
    }

    internal fun handleImportResult(uri: Uri?) {
        uri ?: return
        scope.launch {
            val content = try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)
                        ?.use { inputStream -> inputStream.readBytes().toString(Charsets.UTF_8) }
                } ?: run {
                    onAction(KeywordsUiContract.Action.NotifyImportReadError)
                    return@launch
                }
            } catch (e: Exception) {
                Log.e(TAG, "handleImportResult: exception", e)
                onAction(KeywordsUiContract.Action.NotifyImportReadError)
                return@launch
            }
            onAction(KeywordsUiContract.Action.ImportKeywords(content))
        }
    }

    private fun launchExportPicker(jsonContent: String) {
        pendingExportContent = jsonContent
        val timestamp = LocalDateTime
            .now()
            .format(DateTimeFormatter.ofPattern(EXPORT_FILENAME_DATE_PATTERN))

        exportLauncher.launch(
            input = "${EXPORT_FILENAME_PREFIX}_${timestamp}${EXPORT_FILENAME_EXTENSION}"
        )
    }

    private fun launchImportPicker() {
        importLauncher.launch(arrayOf("application/json"))
    }

}

@Composable
fun rememberKeywordsBackupHelper(
    onAction: (action: KeywordsUiContract.Action) -> Unit,
): KeywordsBackupHelper {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val helper = remember(scope, context) {
        KeywordsBackupHelper(
            context = context,
            scope = scope,
            onAction = onAction,
        )
    }

    helper.exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> helper.handleExportResult(uri) }

    helper.importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri -> helper.handleImportResult(uri) }

    return helper
}
