package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClearKeywordsScreenDataUseCaseTest {

    private val alarmController = mockk<AlarmController>(relaxed = true)
    private val monitoringController = mockk<MonitoringController>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val keywordsRepository = mockk<KeywordsRepository>(relaxed = true)

    private lateinit var useCase: ClearKeywordsScreenDataUseCase

    @BeforeEach
    fun setUp() {
        useCase = ClearKeywordsScreenDataUseCaseImpl(
            alarmController = alarmController,
            monitoringController = monitoringController,
            settingsRepository = settingsRepository,
            keywordsRepository = keywordsRepository,
        )
    }

    @Test
    fun `clears everything in order stopping alarm, disabling monitoring and resetting cooldown`() =
        runTest {
            every { settingsRepository.isMonitoringActive } returns flowOf(false)
            every { monitoringController.isMonitoringRunning } returns flowOf(false)

            useCase()

            coVerifyOrder {
                alarmController.stopAlarm()
                settingsRepository.setMonitoringActive(false)
                settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(0L)
                keywordsRepository.clearAllKeywordsPreferences()
            }
        }

    @Test
    fun `stops running monitoring so runtime matches the now-disabled setting`() = runTest {
        every { settingsRepository.isMonitoringActive } returns flowOf(false)
        every { monitoringController.isMonitoringRunning } returns flowOf(true)

        useCase()

        verify(exactly = 1) { monitoringController.stopMonitoring() }
        verify(exactly = 0) { monitoringController.startMonitoring() }
    }

    @Test
    fun `leaves monitoring runtime untouched when it is already stopped`() = runTest {
        every { settingsRepository.isMonitoringActive } returns flowOf(false)
        every { monitoringController.isMonitoringRunning } returns flowOf(false)

        useCase()

        verify(exactly = 0) { monitoringController.startMonitoring() }
        verify(exactly = 0) { monitoringController.stopMonitoring() }
        coVerify(exactly = 1) { keywordsRepository.clearAllKeywordsPreferences() }
    }

}
