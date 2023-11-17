package com.example.http.di

import com.example.http.utils.logger.LogCatLogger
import com.example.http.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class StuffsModule {

    @Provides
    fun providerLogger(): Logger = LogCatLogger
}