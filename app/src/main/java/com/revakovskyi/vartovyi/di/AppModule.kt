package com.revakovskyi.vartovyi.di

import com.revakovskyi.vartovyi.utils.KeywordMatcher
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::KeywordMatcher)

}
