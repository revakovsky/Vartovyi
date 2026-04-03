package com.revakovskyi.vartovyi.data.di

import com.revakovskyi.vartovyi.data.repository.KeywordsRepositoryImpl
import com.revakovskyi.vartovyi.data.repository.LegalConsentRepositoryImpl
import com.revakovskyi.vartovyi.data.repository.LogRepositoryImpl
import com.revakovskyi.vartovyi.data.repository.SettingsRepositoryImpl
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LegalConsentRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {

    singleOf(::KeywordsRepositoryImpl) { bind<KeywordsRepository>() }
    singleOf(::LogRepositoryImpl) { bind<LogRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::LegalConsentRepositoryImpl) { bind<LegalConsentRepository>() }

}
