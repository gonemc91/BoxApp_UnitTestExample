package com.example.http.utils.async

import com.example.http.domain.Success
import com.example.http.testutils.immediateExecutorService
import com.example.http.testutils.runFlowTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test


@ExperimentalCoroutinesApi
class DefaultLazyListenersFactoryTest {



    @Test
    fun createLazyListenersSubject()  = runFlowTest {
        val factory = DefaultLazyListenersFactory()
        val loader: ValueLoader<String, String> = mockk()
        val listener: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg") } returns "result"

        val subject: LazyListenerSubject<String, String> = factory.createdLazyListenerSubject(
            loaderExecutor = immediateExecutorService(),
            handlerExecutor = immediateExecutorService(),
            loader = loader
        )
        subject.addListeners("arg", listener)

        verify {
            listener(Success("result"))
        }
    }
}