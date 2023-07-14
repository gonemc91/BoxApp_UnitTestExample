package com.example.http.app.utils.async

import com.example.http.app.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


typealias ValueLoader<A, T> = (A) -> T?
typealias ValueListener<T> = (Result<T>) -> Unit

/**
 * When at latest one listener is added via [addListener] call, a [loader] is
 * executed and the status of its executions is propagated to the listeners. Adding
 * other listeners with the same arguments doesn't trigger a new load but the existing
 * one is reused. Also the loading is cancelled and resources freed when the last listenr
 * for the specified arguments is removed by calling [removeListener]
 */


class LazyListenerSubject<A : Any, T : Any>(
    //for real server it's better to use cached thread pool.
    private val loaderExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
    //single thread pool to avoid multi-threading issues
    private val handleExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
    private val loader: ValueLoader<A, T>
) {

    private val listeners = mutableListOf<ListenerRecord<A, T>>()
    private val futures = mutableMapOf<A, FutureRecord<T>>()

    /**
     * Add new listener which receive a value produced by [laoder] withe the
     * specified arguments. Value is produced and cashed by the [laoder] when the
     * first listener with the specified arguments has been added. All further listeners
     * will reuse the same cached value without triggering a [loader]
     */

    fun addListeners(argument: A, listener: ValueListener<T>) = handleExecutor.execute{
        val listenerRecord = ListenerRecord(argument, listener)
        listeners.add(listenerRecord)
        val futureRecord = futures[argument]
        if (futureRecord == null) {
            execute(argument)
        } else {
            listener.invoke(futureRecord.lastValue)
        }
    }

    /**
     * Remove the listener. If the last listener is removed for the specified arguments,
     * the value loader is cancelled for this argument.
     */

    fun removeListener(argument: A, listener: ValueListener<T>) = handleExecutor.execute{
        listeners.removeAll{ it.listener == listener && it.arg == argument }
        if (!listeners.any{it.arg == argument}){
            cancel(argument)
        }
    }

    /**
     * Reload all cashed data.
     * [silentMode]- if set to 'true', [Pending] result is not emitted to listeners
     */

    fun reloadAll(silentMode: Boolean = false) = handleExecutor.execute{
        futures.forEach { entry ->
            val argument = entry.key
            val record = entry.value
            restart(argument, record, silentMode)
        }
    }


    /**
     * Reload cashed data for the specified arguments.
     */
    fun reloadArguments(argument: A, silentMode: Boolean = false) = handleExecutor.execute{
        val record = futures[argument] ?: return@execute
        restart(argument,record,silentMode)
    }

    /**
     * Manually update all value without triggering a [loader].
     */

    fun updateAllValues(newValue: T?){
        futures.forEach{
            val result = if (newValue == null) Empty() else Success(newValue)
            it.value.lastValue = result
            publish(it.key, result)
        }
    }


    private fun cancel(argument: A) {
        val record = futures[argument]
        if (record != null){
            futures.remove(argument)
            record.future?.cancel(true)
        }

    }

    private fun execute(argument: A, silentMode: Boolean = false){
        val record = FutureRecord<T>(null, Pending())
        futures[argument] = record
        val future = loaderExecutor.submit{
            try{
                if (!silentMode) publishIfNotCancelled(record, argument, Pending())
                val res = loader(argument)
                if (res == null){
                    publishIfNotCancelled(record, argument, Empty())
                } else {
                    publishIfNotCancelled(record, argument, Success(res))
                }
            }catch (e: Exception){
                publishIfNotCancelled(record, argument, Error(e))
            }
        }
        record.future = future
    }

    private fun publishIfNotCancelled(record: FutureRecord<T>, argument: A, result: Result<T>){
        if(record.cancelled) return
        publish(argument, result)
    }

    private fun publish(argument: A, result: Result<T>) = handleExecutor.execute{
        futures.filter { it.key == argument }.forEach{
            it.value.lastValue = result
        }
        listeners.filter { it.arg == argument}.forEach{
            it.listener.invoke(result)
        }
    }

    private fun restart(argument: A, record: FutureRecord<T>, silentMode: Boolean){
        record.cancelled = true
        record.future?.cancel(true)
        execute(argument, silentMode)
    }



    private class ListenerRecord<A, T>(
        val arg: A,
        val listener: ValueListener<T>
    )

    private class FutureRecord<T>(
        var future: Future<*>?,
        var lastValue: Result<T> = Empty(),
        var cancelled: Boolean = false
    )



}