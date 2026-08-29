package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.OnboardingRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResetAppToFactoryDefaultsUseCaseTest {

    private val alarmController = mockk<AlarmController>(relaxed = true)
    private val monitoringController = mockk<MonitoringController>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val keywordsRepository = mockk<KeywordsRepository>(relaxed = true)
    private val logRepository = mockk<LogRepository>(relaxed = true)
    private val onboardingRepository = mockk<OnboardingRepository>()

    private lateinit var useCase: ResetAppToFactoryDefaultsUseCase

    @BeforeEach
    fun setUp() {
        every { settingsRepository.isMonitoringActive } returns flowOf(false)
        every { monitoringController.isMonitoringRunning } returns flowOf(false)

        useCase = ResetAppToFactoryDefaultsUseCaseImpl(
            alarmController = alarmController,
            monitoringController = monitoringController,
            settingsRepository = settingsRepository,
            keywordsRepository = keywordsRepository,
            logRepository = logRepository,
            onboardingRepository = onboardingRepository,
        )
    }

    @Test
    fun `restores the placeholder template when no city was ever saved`() = runTest {
        every { onboardingRepository.onboardingCity } returns flowOf("")

        useCase()

        coVerify(exactly = 1) { keywordsRepository.restoreDefaultKeywords(DEFAULT_KEYWORDS_SEED) }
    }

    @Test
    fun `restores the seed with the saved city substituted instead of placeholders`() = runTest {
        every { onboardingRepository.onboardingCity } returns flowOf("Харків")

        useCase()

        coVerify(exactly = 1) {
            keywordsRepository.restoreDefaultKeywords(
                listOf("шахед", "ракета+Харків", "\"ціль на Харків\""),
            )
        }
    }

}
