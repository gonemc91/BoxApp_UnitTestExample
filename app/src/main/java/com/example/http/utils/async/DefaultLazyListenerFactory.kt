package com.example.http.utils.async

import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLazyListenersFactory @Inject constructor() : LazyListenerFactory{

    override fun <A : Any, T : Any> createdLazyListenerSubject(
        loaderExecutor: ExecutorService,
        handlerExecutor: ExecutorService,
        loader: ValueLoader<A, T>,
    ): LazyListenerSubject<A, T> {
       return LazyListenerSubject(
           loaderExecutor = loaderExecutor,
           loader = loader,
           handleExecutor = handlerExecutor
       )
    }
}



