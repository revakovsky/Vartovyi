package com.revakovskyi.vartovyi.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {

    val isOnboardingCompleted: Flow<Boolean>
    val isKeywordsChannelsIntroHidden: Flow<Boolean>
    val onboardingCity: Flow<String>

    suspend fun setOnboardingCompleted()
    suspend fun setKeywordsChannelsIntroHidden()
    suspend fun shouldShowTelegramChannelReminder(): Boolean
    suspend fun setOnboardingCity(city: String)

}
