package com.example.http.utils.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

interface LazyListenerFactory {

    /**
     *Created an instance of [LazyListenerSubject]
     */
    fun <A: Any, T: Any> createdLazyListenerSubject(
        //for real server it's better to use cached thread pool
        loaderExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
        //single thread pool to avoid multi-threading issues
        handlerExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
        loader: ValueLoader<A, T>
    ): LazyListenerSubject<A,T>

}