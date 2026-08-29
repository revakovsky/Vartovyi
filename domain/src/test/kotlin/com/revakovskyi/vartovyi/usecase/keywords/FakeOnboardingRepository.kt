package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal class FakeOnboardingRepository : OnboardingRepository {

    val city = MutableStateFlow("")

    override val isOnboardingCompleted: Flow<Boolean> = flowOf(false)
    override val isKeywordsChannelsIntroHidden: Flow<Boolean> = flowOf(false)
    override val onboardingCity: Flow<String> = city

    override suspend fun setOnboardingCompleted() = Unit

    override suspend fun setKeywordsChannelsIntroHidden() = Unit

    override suspend fun shouldShowTelegramChannelReminder(): Boolean = false

    override suspend fun setOnboardingCity(city: String) {
        this.city.value = city
    }

}
