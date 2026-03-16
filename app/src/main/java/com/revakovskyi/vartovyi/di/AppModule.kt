package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmRetriggerCooldownStateHolder
import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmStateHolder
import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.service.alarm.AlarmControllerImpl
import com.revakovskyi.vartovyi.service.notification_monitoring.MonitoringControllerImpl
import com.revakovskyi.vartovyi.utils.KeywordMatcher
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::KeywordMatcher)
    singleOf(::AlarmStateHolder)
    singleOf(::AlarmRetriggerCooldownStateHolder)

    singleOf(::AlarmControllerImpl) { bind<AlarmController>() }
    singleOf(::MonitoringControllerImpl) { bind<MonitoringController>() }

}
