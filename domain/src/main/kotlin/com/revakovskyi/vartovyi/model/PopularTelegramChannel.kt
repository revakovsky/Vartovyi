package com.revakovskyi.vartovyi.model

/** [displayName] is what gets stored on selection — notification matching works against it. */
data class PopularTelegramChannel(
    val handle: String,
    val displayName: String,
    val region: PopularChannelRegion,
)
