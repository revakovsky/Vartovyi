package com.revakovskyi.vartovyi.ui.screen.troubleshooting.models

import androidx.annotation.StringRes
import com.revakovskyi.vartovyi.R

internal data class TroubleshootingGroup(
    @param:StringRes val titleResId: Int,
    @param:StringRes val bodyResId: Int,
) {
    companion object {
        val all = listOf(
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_xiaomi_miui_title,
                bodyResId = R.string.troubleshooting_xiaomi_miui_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_xiaomi_hyperos_title,
                bodyResId = R.string.troubleshooting_xiaomi_hyperos_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_samsung_title,
                bodyResId = R.string.troubleshooting_samsung_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_oppo_title,
                bodyResId = R.string.troubleshooting_oppo_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_huawei_title,
                bodyResId = R.string.troubleshooting_huawei_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_vivo_title,
                bodyResId = R.string.troubleshooting_vivo_body,
            ),
            TroubleshootingGroup(
                titleResId = R.string.troubleshooting_other_title,
                bodyResId = R.string.troubleshooting_other_body,
            ),
        )
    }
}
