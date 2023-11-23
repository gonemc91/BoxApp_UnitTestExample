package com.example.http.utils.async


import com.example.http.domain.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking


typealias SuspendValueLoader<A, T> = suspend (A) -> T?


/**
 * The same as [LazyListenerSubject] but adapter for using with kotlin flows.
 * @see LazyListenerSubject
 */

class LazyFlowSubject<A: Any, T : Any>(
    lazyListenerFactory: LazyListenerFactory,
    private val loader: SuspendValueLoader<A, T>
) {

    private val lazyListenerSubject = lazyListenerFactory.createdLazyListenerSubject<A, T> {arg->
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
        val listener: ValueListener<T> = { result ->
            trySend(result)
        }
        lazyListenerSubject.addListeners(argument, listener)
        awaitClose {
            lazyListenerSubject.removeListener(argument, listener)
        }

    }




}