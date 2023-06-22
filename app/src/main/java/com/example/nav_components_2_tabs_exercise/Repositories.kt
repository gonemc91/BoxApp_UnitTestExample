package com.example.nav_components_2_tabs_exercise

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.model.accounts.SQLiteAccountRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.BoxesRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.SQLiteBoxesRepository
import com.example.nav_components_2_tabs_exercise.model.settings.AppSettings
import com.example.nav_components_2_tabs_exercise.model.settings.SharedPreferencesAppSettings
import com.example.nav_components_2_tabs_exercise.model.sqlite.AppSQLiteHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {

    private lateinit var applicationContext: Context

    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        AppSQLiteHelper(applicationContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSetting: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }
    //----repositories


    val accountsRepository: AccountsRepository by lazy {
        SQLiteAccountRepository(database, appSetting, ioDispatcher)
    }


    val boxesRepository: BoxesRepository by lazy {
        SQLiteBoxesRepository(database, accountsRepository, ioDispatcher)
    }
    /**
     * Call this method in all application components that may be created at app startup/restoring
     * (e.g. in onCreate of activities and services)
     */

    fun init(context: Context){
        applicationContext = context
    }

}