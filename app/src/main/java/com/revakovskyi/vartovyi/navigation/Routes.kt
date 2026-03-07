package com.revakovskyi.vartovyi.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Home : Routes

    @Serializable
    data object Keywords : Routes

    @Serializable
    data object Log : Routes

    @Serializable
    data object Settings : Routes

    @Serializable
    data object Permissions : Routes

}
