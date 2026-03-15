package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.data.repository.AlarmControllerImpl
import com.revakovskyi.vartovyi.data.repository.KeywordsRepositoryImpl
import com.revakovskyi.vartovyi.data.repository.LogRepositoryImpl
import com.revakovskyi.vartovyi.data.repository.MonitoringControllerImpl
import com.revakovskyi.vartovyi.data.repository.SettingsRepositoryImpl
import com.revakovskyi.vartovyi.domain.repository.AlarmController
import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import com.revakovskyi.vartovyi.domain.repository.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {

    single<AlarmController> { AlarmControllerImpl(androidContext()) }
    single<MonitoringController> { MonitoringControllerImpl(androidContext()) }
    singleOf(::KeywordsRepositoryImpl) { bind<KeywordsRepository>() }
    singleOf(::LogRepositoryImpl) { bind<LogRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }

}
