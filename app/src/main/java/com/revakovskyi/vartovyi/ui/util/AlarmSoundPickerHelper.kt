package com.revakovskyi.vartovyi.ui.util

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri

object AlarmSoundPickerHelper {

    fun createAlarmSoundPickerIntent(
        existingAlarmSoundUri: String,
        pickerTitle: String,
    ): Intent {
        val existingUri = existingAlarmSoundUri
            .takeIf { it.isNotBlank() }
            ?.toUri()
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        return Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_ALARM,
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                pickerTitle,
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                true,
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
                false,
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                existingUri,
            )
        }
    }

    fun parsePickedAlarmSoundUri(intent: Intent?): Uri? {
        if (intent == null) return null

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                Uri::class.java,
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }
    }

    fun resolveAlarmSoundTitle(
        context: Context,
        alarmSoundUri: String,
        defaultAlarmSoundTitle: String,
    ): String {
        val selectedUri = alarmSoundUri
            .takeIf { it.isNotBlank() }
            ?.toUri()
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: return defaultAlarmSoundTitle

        return runCatching {
            RingtoneManager.getRingtone(context, selectedUri)?.getTitle(context)
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: defaultAlarmSoundTitle
    }

}
