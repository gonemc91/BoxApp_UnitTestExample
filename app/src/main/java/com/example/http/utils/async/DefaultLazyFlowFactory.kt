package com.example.http.utils.async

import javax.inject.Inject
import javax.inject.Singleton


/**
 * This module provides factories for creating:
 * - [LazyListenersFactory]
 * - [LazyFlowSubject]
 *
 */
@Singleton
class DefaultLazyFlowFactory @Inject constructor(
    private val lazyListenerFactory: LazyListenerFactory,
) : LazyFlowFactory {
    override fun <A : Any, T : Any> createLazyFlowSubject(
        loader: SuspendValueLoader<A, T>,
    ): LazyFlowSubject<A, T> {
        return LazyFlowSubject(lazyListenerFactory, loader)
    }
}