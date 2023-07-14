package com.example.http.app.utils.async

import kotlinx.coroutines.runBlocking


typealias SuspendValueLoader<A, T> = suspend (A) -> T?


/**
 * The same as [LazyListenerSubject] but adapter for using with kotlin flows.
 * @see LazyListenerSubject
 */

class LazyFlowSubject<A: Any, T : Any>(
    private val loader: SuspendValueLoader<A, T>
) {

    private val lazyListenerSubject = LazyListenerSubject<A, T> {arg->
        runBlocking {
            loader.invoke(arg)
        }
    }

    /**
     * @see [LazyListenerSubject.reloadAll]
     */

    fun reloadAll(silentMode: Boolean = false){
        lazyListenerSubject.reloadAll(silentMode)
    }

    /**
     * @see [LazyListenerSubject.reloadArguments]
     */

    fun reloadArguments(argument: A, silentMode: Boolean = false){
        lazyListenerSubject.reloadArguments(argument,silentMode)
    }



}