package com.example.http.di

import com.example.http.utils.async.DefaultLazyFlowFactory
import com.example.http.utils.async.DefaultLazyListenersFactory
import com.example.http.utils.async.LazyFlowFactory
import com.example.http.utils.async.LazyListenerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

interface LazyFlowFactoryModule {

    @Binds
    fun bindsLazyFlowFactory(
        factory: DefaultLazyFlowFactory
    ): LazyFlowFactory

    @Binds
    fun bindLazyListenerFactory(
        factory: DefaultLazyListenersFactory
    ): LazyListenerFactory
}