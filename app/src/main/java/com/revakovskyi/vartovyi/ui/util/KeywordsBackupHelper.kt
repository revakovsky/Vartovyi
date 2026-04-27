package com.revakovskyi.vartovyi.ui.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
private const val MAX_BACKUP_FILE_SIZE_BYTES = 2 * 1024 * 1024L
private const val FILE_SIZE_UNKNOWN = -1L

@Stable
class KeywordsBackupHelper(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onAction: (action: KeywordsUiContract.Action) -> Unit,
) {

    internal var exportLauncher: ActivityResultLauncher<String>? = null
    internal var importLauncher: ActivityResultLauncher<Array<String>>? = null

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
            val importReadResult = try {
                withContext(Dispatchers.IO) { readImportContent(uri) }
            } catch (e: Exception) {
                Log.e(TAG, "handleImportResult: exception", e)
                onAction(KeywordsUiContract.Action.NotifyImportReadError)
                return@launch
            }

            when (importReadResult) {
                is ImportReadResult.Success -> {
                    onAction(KeywordsUiContract.Action.ImportKeywords(importReadResult.content))
                }

                is ImportReadResult.TooLarge -> {
                    onAction(KeywordsUiContract.Action.NotifyImportFileTooLarge)
                }

                is ImportReadResult.ReadError -> {
                    onAction(KeywordsUiContract.Action.NotifyImportReadError)
                }
            }
        }
    }

    private fun readImportContent(uri: Uri): ImportReadResult {
        val sizeBytes = resolveFileSizeBytes(uri)
        if (sizeBytes > MAX_BACKUP_FILE_SIZE_BYTES) {
            return ImportReadResult.TooLarge
        }

        val content = context.contentResolver.openInputStream(uri)
            ?.bufferedReader(Charsets.UTF_8)
            ?.use { bufferedReader -> bufferedReader.readText() }
            ?: return ImportReadResult.ReadError

        return ImportReadResult.Success(content = content)
    }

    private fun resolveFileSizeBytes(uri: Uri): Long {
        val projection = arrayOf(OpenableColumns.SIZE)

        return context.contentResolver.query(
            /* uri = */ uri,
            /* projection = */ projection,
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder = */ null,
        )?.use { cursor ->
            val sizeColumnIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeColumnIndex < 0 || !cursor.moveToFirst()) {
                return@use FILE_SIZE_UNKNOWN
            }

            cursor.getLong(sizeColumnIndex)
        } ?: FILE_SIZE_UNKNOWN
    }

    private sealed interface ImportReadResult {
        data class Success(val content: String) : ImportReadResult
        data object TooLarge : ImportReadResult
        data object ReadError : ImportReadResult
    }

    private fun launchExportPicker(jsonContent: String) {
        pendingExportContent = jsonContent
        val timestamp = LocalDateTime
            .now()
            .format(DateTimeFormatter.ofPattern(EXPORT_FILENAME_DATE_PATTERN))

        exportLauncher?.launch(
            input = "${EXPORT_FILENAME_PREFIX}_${timestamp}${EXPORT_FILENAME_EXTENSION}"
        )
    }

    private fun launchImportPicker() {
        importLauncher?.launch(arrayOf("application/json"))
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
