package com.example.http.utils.async

import com.example.http.domain.Pending
import com.example.http.domain.Success
import com.example.http.testutils.immediateExecutorService
import com.example.http.testutils.runFlowTest
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class LazyFlowSubjectTest {

    @get: Rule

    val rule = MockKRule(this)

    @RelaxedMockK
    lateinit var lazyListenersSubject: LazyListenerSubject<String, String>

    @MockK
    lateinit var lazyListenerFactory: LazyListenerFactory

    lateinit var loader: SuspendValueLoader<String, String>

    lateinit var lazyFlowSubject: LazyFlowSubject<String, String>

    @Before
    fun setUp() {
        loader = mockk(relaxed = true)
        every {
            lazyListenerFactory.createdLazyListenerSubject<String, String> (any(),any(),any())
        }returns lazyListenersSubject

        lazyFlowSubject = LazyFlowSubject(lazyListenerFactory, loader)

        mockkStatic(Executors::class)
        every { Executors.newSingleThreadExecutor() } returns immediateExecutorService()
    }

    @After
    fun tearDown(){
        unmockkStatic(Executors::class)
    }

    @Test
    fun initCreateLazyListenersSubjectWithValidLoader(){
        val slot: CapturingSlot<ValueLoader<String, String>> = slot()
        every {
            lazyListenerFactory.createdLazyListenerSubject(any(), any(), capture(slot))
        } returns mockk()
        coEvery { loader("arg") } returns "result"

        LazyFlowSubject(lazyListenerFactory, loader)
        val answer = slot.captured("arg")

        Assert.assertEquals("result", answer)
    }

     @Test
     fun reloadAllDelegatesCallToLazyListenerSubject(){
         lazyFlowSubject.reloadAll(silentMode = true)

         verify(exactly = 1) {
             lazyListenersSubject.reloadAll(silentMode = true)
         }
     }

    @Test
    fun reloadAllDoesNotUseSilentModeByDefault(){
        lazyFlowSubject.reloadAll()

        verify(exactly = 1) {
            lazyListenersSubject.reloadAll(silentMode = false)
        }
    }

    @Test
    fun updateAllValuesDelegatesCallToLazyListenersSubject() {

        lazyFlowSubject.updateAllValues("test")

        verify(exactly = 1) {
            lazyListenersSubject.updateAllValues("test")
        }
    }

    @Test
    fun listenDeliversResultsFromCallbackToFlow() = runFlowTest {
        val slot = capturedAddListeners("arg")

        val flow = lazyFlowSubject.listen("arg")
        val results = flow.startCollecting()
        slot.captured(Pending())
        slot.captured(Success("hi"))


        Assert.assertEquals(
            listOf(Pending(), Success("hi")),
            results
        )
    }
    @Test
    fun listenAfterCancellingSubscriptionRemoveCallback() = runFlowTest {
        val slot = capturedAddListeners("arg")

        val flow = lazyFlowSubject.listen("arg")
        val result = flow.startCollecting()
        slot.captured(Success("111"))
        flow.cancelCollecting()
        slot.captured(Success("222"))

        Assert.assertEquals(
            listOf(Success("111")),
            result
        )
        verify(exactly = 1) {
            lazyListenersSubject.removeListener("arg", refEq(slot.captured))
        }
    }


    private fun capturedAddListeners(arg: String): CapturingSlot<ValueListener<String>>{
        val slot: CapturingSlot<ValueListener<String>> = slot()
        every {
            lazyListenersSubject.addListeners(arg,capture(slot))
        } just runs
        return slot
    }


}