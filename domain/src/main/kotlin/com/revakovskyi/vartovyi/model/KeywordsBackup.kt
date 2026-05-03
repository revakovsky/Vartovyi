package com.revakovskyi.vartovyi.model

import kotlinx.serialization.Serializable

@Serializable
data class KeywordsBackup(
    val version: Int = CURRENT_VERSION,
    val keywords: List<String>,
    val stopWords: List<String>,
    val telegramChannels: List<String>,
    val isTelegramChannelFilterEnabled: Boolean,
) {

    companion object {
        const val CURRENT_VERSION = 1
    }

}
