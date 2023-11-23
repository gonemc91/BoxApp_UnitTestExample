package com.example.http.utils.async

import com.example.http.domain.Pending
import com.example.http.domain.Success
import com.example.http.testutils.immediateExecutorService
import com.example.http.testutils.runFlowTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class DefaultLazyFlowFactoryTest {
    @Before
    fun setUp() {
        mockkStatic(Executors::class)
        every { Executors.newSingleThreadExecutor() } returns immediateExecutorService()
    }

    @After
    fun tearDown() {
        unmockkStatic(Executors::class)
    }

    @Test
    fun createLazyFlowSubject() = runFlowTest {
        val factory = DefaultLazyFlowFactory(DefaultLazyListenersFactory())
        val loader: SuspendValueLoader<String, String> = mockk()
        coEvery { loader("arg") } returns "result"

        val subject: LazyFlowSubject<String, String> =
            factory.createLazyFlowSubject(loader)
        val collectedResults =
            subject.listen("arg").startCollecting()


        Assert.assertEquals(
            listOf(Pending(), Success("result")),
            collectedResults
        )

    }
}