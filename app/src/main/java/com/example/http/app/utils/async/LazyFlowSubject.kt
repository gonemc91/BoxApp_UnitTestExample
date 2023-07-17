package com.example.http.app.utils.async


import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.http.app.Result


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
     * @see LazyListenerSubject.reloadAll
     */

    fun reloadAll(silentMode: Boolean = false){
        lazyListenerSubject.reloadAll(silentMode)
    }

    /**
     * @see LazyListenerSubject.reloadArguments
     */

    fun reloadArguments(argument: A, silentMode: Boolean = false){
        lazyListenerSubject.reloadArguments(argument,silentMode)
    }

    /**
     * @see LazyListenerSubject.updateAllValues
     *
     */
    fun updateAllValues(newValue: T?){
        lazyListenerSubject.updateAllValues(newValue)
    }

    /**
     * @see LazyListenerSubject.addListeners
     * @see LazyListenerSubject.removeListener
     */

    fun listen(argument: A): Flow<Result<T>> = callbackFlow{
        val listener: ValueListener<T> = {result ->
            trySend(result)
        }
        lazyListenerSubject.addListeners(argument, listener)
        awaitClose {
            lazyListenerSubject.removeListener(argument, listener)
        }

    }




}