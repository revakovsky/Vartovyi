package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.contract.CrashReporter
import com.revakovskyi.vartovyi.contract.ElapsedRealtimeProvider
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.alarm.AlarmRetriggerCooldownStateHolder
import com.revakovskyi.vartovyi.controllers.alarm.AlarmStateHolder
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.service.alarm.AlarmControllerImpl
import com.revakovskyi.vartovyi.service.notification_monitoring.MonitoringControllerImpl
import com.revakovskyi.vartovyi.utils.CrashlyticsCrashReporter
import com.revakovskyi.vartovyi.utils.ElapsedRealtimeProviderImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::ElapsedRealtimeProviderImpl) { bind<ElapsedRealtimeProvider>() }
    singleOf(::CrashlyticsCrashReporter) { bind<CrashReporter>() }
    singleOf(::AlarmStateHolder)
    singleOf(::AlarmRetriggerCooldownStateHolder)

    singleOf(::AlarmControllerImpl) { bind<AlarmController>() }
    singleOf(::MonitoringControllerImpl) { bind<MonitoringController>() }

}
