package com.revakovskyi.vartovyi.data.di

import androidx.room.Room
import com.revakovskyi.vartovyi.data.datastore.KeywordsDataStore
import com.revakovskyi.vartovyi.data.datastore.LegalConsentDataStore
import com.revakovskyi.vartovyi.data.datastore.MonitoringDataStore
import com.revakovskyi.vartovyi.data.db.VartovyiDatabase
import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private const val DATABASE_NAME = "vartovyi_database"

val databaseModule = module {

    singleOf(::MonitoringDataStore)
    singleOf(::KeywordsDataStore)
    singleOf(::LegalConsentDataStore)

    single<VartovyiDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = VartovyiDatabase::class.java,
            name = DATABASE_NAME,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single<AlertEventDao> { get<VartovyiDatabase>().alertEventDao() }

}
