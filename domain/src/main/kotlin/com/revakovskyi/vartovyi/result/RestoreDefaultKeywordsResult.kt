package com.revakovskyi.vartovyi.result

sealed interface RestoreDefaultKeywordsResult {

    data class Added(val count: Int) : RestoreDefaultKeywordsResult
    data object NothingAdded : RestoreDefaultKeywordsResult

}
