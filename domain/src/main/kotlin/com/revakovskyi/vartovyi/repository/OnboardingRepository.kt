package com.revakovskyi.vartovyi.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {

    val isOnboardingCompleted: Flow<Boolean>
    val isKeywordsChannelsIntroHidden: Flow<Boolean>

    suspend fun setOnboardingCompleted()
    suspend fun setKeywordsChannelsIntroHidden()

}
