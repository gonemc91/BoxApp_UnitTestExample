package com.example.http.di

import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.sources.accounts.RetrofitAccountSource
import com.example.http.sources.boxes.RetrofitBoxesSources
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {

@Binds
abstract fun bindAccountSource(
    retrofitAccountSource: RetrofitAccountSource
): AccountsSources

@Binds
abstract fun  bindBoxesSource(
    retrofitBoxesSources: RetrofitBoxesSources
): BoxesSource

}