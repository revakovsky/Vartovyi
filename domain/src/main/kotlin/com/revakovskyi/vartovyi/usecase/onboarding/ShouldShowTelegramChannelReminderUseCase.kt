package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository

interface ShouldShowTelegramChannelReminderUseCase {
    suspend operator fun invoke(): Boolean
}

internal class ShouldShowTelegramChannelReminderUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : ShouldShowTelegramChannelReminderUseCase {

    override suspend operator fun invoke(): Boolean {
        return onboardingRepository.shouldShowTelegramChannelReminder()
    }

}
