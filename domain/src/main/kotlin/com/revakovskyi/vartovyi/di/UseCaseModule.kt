package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRetriggerCooldownUseCase
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRetriggerCooldownUseCaseImpl
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRunningUseCaseImpl
import com.revakovskyi.vartovyi.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.usecase.alarm.StopAlarmUseCaseImpl
import com.revakovskyi.vartovyi.usecase.alarm.TriggerAlarmUseCase
import com.revakovskyi.vartovyi.usecase.alarm.TriggerAlarmUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddKeywordUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddStopWordUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddTelegramChannelUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ClearKeywordsScreenDataUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ClearKeywordsScreenDataUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ExportKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ExportKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ImportKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ImportKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveStopWordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveKeywordUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.RemoveStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveStopWordUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.RemoveTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveTelegramChannelUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultStopWordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeKeywordInputUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeKeywordInputUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.SeedDefaultKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SeedDefaultKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.SeedDefaultStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SeedDefaultStopWordsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ToggleTelegramChannelFilterUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ToggleTelegramChannelFilterUseCaseImpl
import com.revakovskyi.vartovyi.usecase.legal.AcceptCurrentLegalDocumentsUseCase
import com.revakovskyi.vartovyi.usecase.legal.AcceptCurrentLegalDocumentsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.legal.ObserveLegalConsentStateUseCase
import com.revakovskyi.vartovyi.usecase.legal.ObserveLegalConsentStateUseCaseImpl
import com.revakovskyi.vartovyi.usecase.log.ClearLogUseCase
import com.revakovskyi.vartovyi.usecase.log.ClearLogUseCaseImpl
import com.revakovskyi.vartovyi.usecase.log.GetLogEntryIndexUseCase
import com.revakovskyi.vartovyi.usecase.log.GetLogEntryIndexUseCaseImpl
import com.revakovskyi.vartovyi.usecase.log.ObserveLastAlarmTriggeredEventUseCase
import com.revakovskyi.vartovyi.usecase.log.ObserveLastAlarmTriggeredEventUseCaseImpl
import com.revakovskyi.vartovyi.usecase.log.ObserveLogEntriesUseCase
import com.revakovskyi.vartovyi.usecase.log.ObserveLogEntriesUseCaseImpl
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCaseImpl
import com.revakovskyi.vartovyi.usecase.monitoring.SyncMonitoringRuntimeUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.SyncMonitoringRuntimeUseCaseImpl
import com.revakovskyi.vartovyi.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ToggleMonitoringUseCaseImpl
import com.revakovskyi.vartovyi.usecase.notification.ProcessIncomingTelegramNotificationUseCase
import com.revakovskyi.vartovyi.usecase.notification.ProcessIncomingTelegramNotificationUseCaseImpl
import com.revakovskyi.vartovyi.usecase.onboarding.ObserveOnboardingCompletedUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.ObserveOnboardingCompletedUseCaseImpl
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCompletedUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCompletedUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.ObserveAlarmRetriggerCooldownDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveAlarmRetriggerCooldownDurationUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.ObserveLogSizeLimitUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveLogSizeLimitUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveScheduleSettingsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.ResetAppToFactoryDefaultsUseCase
import com.revakovskyi.vartovyi.usecase.settings.ResetAppToFactoryDefaultsUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmDurationUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmRetriggerCooldownDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmRetriggerCooldownDurationUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmSoundUriUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmSoundUriUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmVolumeUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmVolumeUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetEndTimeUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetEndTimeUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetLogSizeLimitUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetLogSizeLimitUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetScheduleEnabledUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetScheduleEnabledUseCaseImpl
import com.revakovskyi.vartovyi.usecase.settings.SetStartTimeUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetStartTimeUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {

    singleOf(::TriggerAlarmUseCaseImpl) { bind<TriggerAlarmUseCase>() }
    singleOf(::StopAlarmUseCaseImpl) { bind<StopAlarmUseCase>() }
    singleOf(::ObserveAlarmRunningUseCaseImpl) { bind<ObserveAlarmRunningUseCase>() }
    singleOf(::ObserveAlarmRetriggerCooldownUseCaseImpl) { bind<ObserveAlarmRetriggerCooldownUseCase>() }

    singleOf(::ObserveMonitoringStateUseCaseImpl) { bind<ObserveMonitoringStateUseCase>() }
    singleOf(::SyncMonitoringRuntimeUseCaseImpl) { bind<SyncMonitoringRuntimeUseCase>() }
    singleOf(::ToggleMonitoringUseCaseImpl) { bind<ToggleMonitoringUseCase>() }
    singleOf(::ProcessIncomingTelegramNotificationUseCaseImpl) { bind<ProcessIncomingTelegramNotificationUseCase>() }

    singleOf(::ClearKeywordsScreenDataUseCaseImpl) { bind<ClearKeywordsScreenDataUseCase>() }

    singleOf(::ObserveKeywordsUseCaseImpl) { bind<ObserveKeywordsUseCase>() }
    singleOf(::ObserveStopWordsUseCaseImpl) { bind<ObserveStopWordsUseCase>() }
    singleOf(::AddKeywordUseCaseImpl) { bind<AddKeywordUseCase>() }
    singleOf(::RemoveKeywordUseCaseImpl) { bind<RemoveKeywordUseCase>() }
    singleOf(::AddStopWordUseCaseImpl) { bind<AddStopWordUseCase>() }
    singleOf(::RemoveStopWordUseCaseImpl) { bind<RemoveStopWordUseCase>() }
    singleOf(::ObserveTelegramChannelsUseCaseImpl) { bind<ObserveTelegramChannelsUseCase>() }
    singleOf(::ObserveTelegramChannelFilterEnabledUseCaseImpl) { bind<ObserveTelegramChannelFilterEnabledUseCase>() }
    singleOf(::AddTelegramChannelUseCaseImpl) { bind<AddTelegramChannelUseCase>() }
    singleOf(::RemoveTelegramChannelUseCaseImpl) { bind<RemoveTelegramChannelUseCase>() }
    singleOf(::ToggleTelegramChannelFilterUseCaseImpl) { bind<ToggleTelegramChannelFilterUseCase>() }
    singleOf(::ExportKeywordsUseCaseImpl) { bind<ExportKeywordsUseCase>() }
    singleOf(::ImportKeywordsUseCaseImpl) { bind<ImportKeywordsUseCase>() }
    singleOf(::SeedDefaultKeywordsUseCaseImpl) { bind<SeedDefaultKeywordsUseCase>() }
    singleOf(::SeedDefaultStopWordsUseCaseImpl) { bind<SeedDefaultStopWordsUseCase>() }
    singleOf(::RestoreDefaultKeywordsUseCaseImpl) { bind<RestoreDefaultKeywordsUseCase>() }
    singleOf(::RestoreDefaultStopWordsUseCaseImpl) { bind<RestoreDefaultStopWordsUseCase>() }
    singleOf(::SanitizeKeywordInputUseCaseImpl) { bind<SanitizeKeywordInputUseCase>() }

    singleOf(::ObserveLogEntriesUseCaseImpl) { bind<ObserveLogEntriesUseCase>() }
    singleOf(::ObserveLastAlarmTriggeredEventUseCaseImpl) { bind<ObserveLastAlarmTriggeredEventUseCase>() }
    singleOf(::GetLogEntryIndexUseCaseImpl) { bind<GetLogEntryIndexUseCase>() }
    singleOf(::ClearLogUseCaseImpl) { bind<ClearLogUseCase>() }

    singleOf(::ObserveLegalConsentStateUseCaseImpl) { bind<ObserveLegalConsentStateUseCase>() }
    singleOf(::AcceptCurrentLegalDocumentsUseCaseImpl) { bind<AcceptCurrentLegalDocumentsUseCase>() }
    singleOf(::ObserveOnboardingCompletedUseCaseImpl) { bind<ObserveOnboardingCompletedUseCase>() }
    singleOf(::SetOnboardingCompletedUseCaseImpl) { bind<SetOnboardingCompletedUseCase>() }

    singleOf(::ObserveScheduleSettingsUseCaseImpl) { bind<ObserveScheduleSettingsUseCase>() }
    singleOf(::ObserveLogSizeLimitUseCaseImpl) { bind<ObserveLogSizeLimitUseCase>() }
    singleOf(::ObserveAlarmRetriggerCooldownDurationUseCaseImpl) {
        bind<ObserveAlarmRetriggerCooldownDurationUseCase>()
    }
    singleOf(::SetScheduleEnabledUseCaseImpl) { bind<SetScheduleEnabledUseCase>() }
    singleOf(::SetStartTimeUseCaseImpl) { bind<SetStartTimeUseCase>() }
    singleOf(::SetEndTimeUseCaseImpl) { bind<SetEndTimeUseCase>() }
    singleOf(::SetAlarmDurationUseCaseImpl) { bind<SetAlarmDurationUseCase>() }
    singleOf(::SetAlarmSoundUriUseCaseImpl) { bind<SetAlarmSoundUriUseCase>() }
    singleOf(::SetAlarmVolumeUseCaseImpl) { bind<SetAlarmVolumeUseCase>() }
    singleOf(::SetLogSizeLimitUseCaseImpl) { bind<SetLogSizeLimitUseCase>() }
    singleOf(::SetAlarmRetriggerCooldownDurationUseCaseImpl) { bind<SetAlarmRetriggerCooldownDurationUseCase>() }

    singleOf(::ResetAppToFactoryDefaultsUseCaseImpl) { bind<ResetAppToFactoryDefaultsUseCase>() }

}
