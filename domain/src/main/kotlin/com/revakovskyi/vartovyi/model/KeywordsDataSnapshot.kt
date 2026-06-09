package com.revakovskyi.vartovyi.model

data class KeywordsDataSnapshot(
    val keywords: List<String>,
    val stopWords: List<String>,
    val telegramChannels: List<String>,
    val isTelegramChannelFilterEnabled: Boolean,
)
