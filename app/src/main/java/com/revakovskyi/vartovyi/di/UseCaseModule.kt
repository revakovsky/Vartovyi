package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.domain.usecase.AnalyzeMessageUseCase
import com.revakovskyi.vartovyi.domain.usecase.AnalyzeMessageUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.domain.usecase.alarm.ObserveAlarmRunningUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.domain.usecase.alarm.StopAlarmUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.alarm.TriggerAlarmUseCase
import com.revakovskyi.vartovyi.domain.usecase.alarm.TriggerAlarmUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddKeywordUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddStopWordUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddTelegramChannelUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveStopWordsUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelsUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveKeywordUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveStopWordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveStopWordUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveTelegramChannelUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveTelegramChannelUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.keywords.ToggleTelegramChannelFilterUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ToggleTelegramChannelFilterUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.log.AddLogEntryUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.AddLogEntryUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.log.ClearLogUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ClearLogUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ObserveMonitoringStateUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ToggleMonitoringUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveLogSizeLimitUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveLogSizeLimitUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveTelegramPackagesUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveTelegramPackagesUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetAlarmDurationUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetAlarmDurationUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetEndTimeUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetEndTimeUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetLogSizeLimitUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetLogSizeLimitUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetScheduleEnabledUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetScheduleEnabledUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetStartTimeUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetStartTimeUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetTelegramPackagesUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetTelegramPackagesUseCaseImpl
import com.revakovskyi.vartovyi.domain.usecase.settings.SetVibrationEnabledUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetVibrationEnabledUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {

    singleOf(::AnalyzeMessageUseCaseImpl) { bind<AnalyzeMessageUseCase>() }
    singleOf(::TriggerAlarmUseCaseImpl) { bind<TriggerAlarmUseCase>() }
    singleOf(::StopAlarmUseCaseImpl) { bind<StopAlarmUseCase>() }
    singleOf(::ObserveAlarmRunningUseCaseImpl) { bind<ObserveAlarmRunningUseCase>() }

    singleOf(::ObserveMonitoringStateUseCaseImpl) { bind<ObserveMonitoringStateUseCase>() }
    singleOf(::ToggleMonitoringUseCaseImpl) { bind<ToggleMonitoringUseCase>() }

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

    singleOf(::AddLogEntryUseCaseImpl) { bind<AddLogEntryUseCase>() }
    singleOf(::ObserveLogEntriesUseCaseImpl) { bind<ObserveLogEntriesUseCase>() }
    singleOf(::ClearLogUseCaseImpl) { bind<ClearLogUseCase>() }

    singleOf(::ObserveScheduleSettingsUseCaseImpl) { bind<ObserveScheduleSettingsUseCase>() }
    singleOf(::ObserveTelegramPackagesUseCaseImpl) { bind<ObserveTelegramPackagesUseCase>() }
    singleOf(::ObserveLogSizeLimitUseCaseImpl) { bind<ObserveLogSizeLimitUseCase>() }
    singleOf(::SetScheduleEnabledUseCaseImpl) { bind<SetScheduleEnabledUseCase>() }
    singleOf(::SetStartTimeUseCaseImpl) { bind<SetStartTimeUseCase>() }
    singleOf(::SetEndTimeUseCaseImpl) { bind<SetEndTimeUseCase>() }
    singleOf(::SetAlarmDurationUseCaseImpl) { bind<SetAlarmDurationUseCase>() }
    singleOf(::SetVibrationEnabledUseCaseImpl) { bind<SetVibrationEnabledUseCase>() }
    singleOf(::SetTelegramPackagesUseCaseImpl) { bind<SetTelegramPackagesUseCase>() }
    singleOf(::SetLogSizeLimitUseCaseImpl) { bind<SetLogSizeLimitUseCase>() }

}
