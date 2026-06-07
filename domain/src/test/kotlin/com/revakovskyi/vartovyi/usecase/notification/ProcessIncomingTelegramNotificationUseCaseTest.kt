package com.revakovskyi.vartovyi.usecase.notification

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.contract.ElapsedRealtimeProvider
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.model.NotificationPayload
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private fun testPayload(
    title: String = "Тестовий канал",
    text: String,
): NotificationPayload {
    return NotificationPayload(
        packageName = "org.telegram.messenger",
        notificationKey = "key-1",
        title = title,
        text = text,
        timestamp = 1_700_000_000_000L,
    )
}

class ProcessIncomingTelegramNotificationUseCaseTest {

    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val keywordsRepository = mockk<KeywordsRepository>(relaxed = true)
    private val logRepository = mockk<LogRepository>(relaxed = true)
    private val alarmController = mockk<AlarmController>(relaxed = true)
    private val elapsedRealtimeProvider = mockk<ElapsedRealtimeProvider>()

    private lateinit var useCase: ProcessIncomingTelegramNotificationUseCase

    @BeforeEach
    fun setUp() {
        every { settingsRepository.isMonitoringActive } returns flowOf(true)
        every { settingsRepository.isScheduleEnabled } returns flowOf(false)
        every { settingsRepository.startTime } returns flowOf("08:00")
        every { settingsRepository.endTime } returns flowOf("20:00")
        every { settingsRepository.logSizeLimit } returns flowOf(100)
        every { settingsRepository.alarmRetriggerCooldownDurationMillis } returns flowOf(60_000L)
        every {
            settingsRepository.alarmRetriggerCooldownUntilElapsedRealtimeMillis
        } returns flowOf(0L)
        every { keywordsRepository.isTelegramChannelFilterEnabled } returns flowOf(false)
        every { keywordsRepository.telegramChannels } returns flowOf(emptyList())
        every { keywordsRepository.keywords } returns flowOf(listOf("ракета"))
        every { keywordsRepository.stopWords } returns flowOf(emptyList())
        every { alarmController.isAlarmRunning } returns flowOf(false)
        coEvery {
            logRepository.addEntryAndTrimToLimit(
                event = any(),
                notificationKey = any(),
                limit = any(),
            )
        } returns true
        every { elapsedRealtimeProvider.now() } returns 1_000L

        useCase = ProcessIncomingTelegramNotificationUseCaseImpl(
            settingsRepository = settingsRepository,
            keywordsRepository = keywordsRepository,
            logRepository = logRepository,
            alarmController = alarmController,
            elapsedRealtimeProvider = elapsedRealtimeProvider,
        )
    }

    @Nested
    inner class StopWordSuppression {

        @Test
        fun `keyword triggers the alarm when no stop word matches`() = runTest {
            every { keywordsRepository.stopWords } returns flowOf(listOf("розвід"))

            val result = useCase(testPayload(text = "Ракета над містом"))

            assertThat(result).isTrue()
            verify(exactly = 1) { alarmController.triggerAlarm(any(), any()) }
        }

        @Test
        fun `stop word with another apostrophe variant suppresses the alarm`() = runTest {
            every { keywordsRepository.stopWords } returns flowOf(listOf("обʼєкт"))

            val result = useCase(testPayload(text = "Об'єкт уражено, ракета летить"))

            assertThat(result).isFalse()
            verify(exactly = 0) { alarmController.triggerAlarm(any(), any()) }
        }

        @Test
        fun `legacy stop word with straight apostrophe suppresses typographic apostrophe text`() =
            runTest {
                every { keywordsRepository.stopWords } returns flowOf(listOf("об'єкт"))

                val result = useCase(testPayload(text = "Об’єкт уражено, ракета летить"))

                assertThat(result).isFalse()
                verify(exactly = 0) { alarmController.triggerAlarm(any(), any()) }
            }

        @Test
        fun `stop word suppresses text with doubled spaces`() = runTest {
            every { keywordsRepository.stopWords } returns flowOf(listOf("навчальна тривога"))

            val result = useCase(testPayload(text = "Навчальна  тривога, ракета умовна"))

            assertThat(result).isFalse()
            verify(exactly = 0) { alarmController.triggerAlarm(any(), any()) }
        }
    }

    @Nested
    inner class ChannelFilter {

        @BeforeEach
        fun enableFilter() {
            every { keywordsRepository.isTelegramChannelFilterEnabled } returns flowOf(true)
        }

        @Test
        fun `channel with another apostrophe variant passes the filter`() = runTest {
            every { keywordsRepository.telegramChannels } returns flowOf(listOf("Обʼєднані сили"))

            val result = useCase(
                testPayload(title = "Об'єднані сили", text = "Ракета над містом")
            )

            assertThat(result).isTrue()
            verify(exactly = 1) { alarmController.triggerAlarm(any(), any()) }
        }

        @Test
        fun `emoji-decorated incoming title matches plain stored channel`() = runTest {
            every {
                keywordsRepository.telegramChannels
            } returns flowOf(listOf("Повітряні цілі Київ"))

            val result = useCase(
                testPayload(title = "🚨 Повітряні цілі Київ", text = "Ракета над містом")
            )

            assertThat(result).isTrue()
            verify(exactly = 1) { alarmController.triggerAlarm(any(), any()) }
        }

        @Test
        fun `stored channel with doubled spaces matches single-spaced title`() = runTest {
            every { keywordsRepository.telegramChannels } returns flowOf(listOf("Повітряні  Сили"))

            val result = useCase(
                testPayload(title = "Повітряні Сили", text = "Ракета над містом")
            )

            assertThat(result).isTrue()
            verify(exactly = 1) { alarmController.triggerAlarm(any(), any()) }
        }

        @Test
        fun `unknown channel is blocked when filter is enabled`() = runTest {
            every { keywordsRepository.telegramChannels } returns flowOf(listOf("Кохана"))

            val result = useCase(
                testPayload(title = "Інший канал", text = "Ракета над містом")
            )

            assertThat(result).isFalse()
            verify(exactly = 0) { alarmController.triggerAlarm(any(), any()) }
        }
    }
}
