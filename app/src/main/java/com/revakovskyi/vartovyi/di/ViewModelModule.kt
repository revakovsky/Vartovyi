package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.MainViewModel
import com.revakovskyi.vartovyi.ui.screen.home.HomeViewModel
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsViewModel
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentViewModel
import com.revakovskyi.vartovyi.ui.screen.log.LogViewModel
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingViewModel
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsViewModel
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::KeywordsViewModel)
    viewModelOf(::LogViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PermissionsViewModel)
    viewModelOf(::LegalConsentViewModel)
    viewModelOf(::OnboardingViewModel)

}
