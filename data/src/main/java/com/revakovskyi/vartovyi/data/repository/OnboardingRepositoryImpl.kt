package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.datastore.OnboardingDataStore
import com.revakovskyi.vartovyi.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

internal class OnboardingRepositoryImpl(
    private val onboardingDataStore: OnboardingDataStore,
) : OnboardingRepository {

    override val isOnboardingCompleted: Flow<Boolean> =
        onboardingDataStore.isOnboardingCompleted

    override suspend fun setOnboardingCompleted() {
        onboardingDataStore.setOnboardingCompleted()
    }

}
