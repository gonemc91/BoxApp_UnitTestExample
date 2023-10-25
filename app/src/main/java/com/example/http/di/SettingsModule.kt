package com.example.http.di

import com.example.http.app.model.settings.AppSettings
import com.example.http.app.model.settings.SharedPreferencesAppSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//Get realization AppSettings

@Module //annotation Module
@InstallIn(SingletonComponent::class) //SingletonComponent build-in Hilt components(global dependencies)
abstract class SettingsModule {

    @Binds // связывание
    /*@Singleton*/ //scope var 1 for library
    abstract fun bindAppSettings(
        appSettings: SharedPreferencesAppSettings
    ): AppSettings//instruction for hilt. If need interface AppSettings get realization on SharedPreferenceAppSettings

}