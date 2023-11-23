package com.example.http.di

import com.example.http.domain.accounts.AccountsSource
import com.example.http.domain.boxes.BoxesSource
import com.example.http.data.accounts.RetrofitAccountSource
import com.example.http.data.boxes.RetrofitBoxesSources
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
): AccountsSource

@Binds
abstract fun  bindBoxesSource(
    retrofitBoxesSources: RetrofitBoxesSources
): BoxesSource

}