package com.example.nav_components_2_tabs_exercise

import android.content.Context
import androidx.room.Room
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.model.accounts.room.RoomAccountsRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.BoxesRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.room.RoomBoxesRepository
import com.example.nav_components_2_tabs_exercise.model.room.AppDatabase
import com.example.nav_components_2_tabs_exercise.model.settings.AppSettings
import com.example.nav_components_2_tabs_exercise.model.settings.SharedPreferencesAppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {

    private lateinit var applicationContext: Context


    // Create an AppDatabase instance by using Room.databaseBuilder static method. " +
    //   "Use createFromAssets method to initialize a new database from the pre-packaged SQLite file from assets")

    private val database: AppDatabase by lazy<AppDatabase> {
     Room.databaseBuilder(applicationContext, AppDatabase::class.java,"database.db")
         .createFromAsset("initial_database.db")
         .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSetting: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }
    //----repositories


    val accountsRepository: AccountsRepository by lazy{
        RoomAccountsRepository(database.getAccountDao(), appSetting, ioDispatcher)
    }


    val boxesRepository: BoxesRepository by lazy {
        RoomBoxesRepository(accountsRepository, database.getBoxesDao(), ioDispatcher)
    }
    /**
     * Call this method in all application components that may be created at app startup/restoring
     * (e.g. in onCreate of activities and services)
     */

    fun init(context: Context){
        applicationContext = context
    }

}