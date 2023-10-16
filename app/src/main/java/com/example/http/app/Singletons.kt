package com.example.http.app

import android.content.Context
import com.example.http.app.model.SourcesProvider
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesRepository
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.app.model.settings.AppSettings
import com.example.http.app.model.settings.SharedPreferencesAppSettings

object Singletons {

    private lateinit var appContext: Context

    private val sourceProvider: SourcesProvider by lazy{
    TODO()
    }

    val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    // --- sources

    private val  accountSource: AccountsSources by lazy {
        sourceProvider.getAccountsSource()
    }

    private val boxesSource: BoxesSource by lazy {
        sourceProvider.getBoxesSources()
    }

    // --- repositories

    val accountsRepository: AccountsRepository by lazy {
        AccountsRepository(
            accountSource = accountSource,
            appSettings = appSettings
        )
    }

    val boxesRepository: BoxesRepository by lazy {
        BoxesRepository(
            accountsRepository = accountsRepository,
            boxesSource = boxesSource
        )
    }

        /**
         * Call this method in all application components that may be created at app startup/restoring
         * (e.g. in onCreate of activities and services)
         */

        fun init(appContext: Context){
            Singletons.appContext = appContext
    }
}



