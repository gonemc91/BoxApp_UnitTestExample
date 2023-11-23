package com.example.http.utils.async

import com.example.http.domain.Empty
import com.example.http.domain.Error
import com.example.http.domain.Pending
import com.example.http.domain.Success
import com.example.http.testutils.immediateExecutorService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LazyListenerSubjectTest {

    @get: Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var loader: ValueLoader<String, String>

    @RelaxedMockK
    lateinit var listener: ValueListener<String>


    @Test
    fun addListenerTriggersValueLoadingOnlyOnceForSameArgument(){
        val subject = createSubject()
        every { loader(any()) } returns "result"

        subject.addListeners("arg1", listener)
        subject.addListeners("arg1", listener)
        subject.addListeners("arg2", listener)
        subject.addListeners("arg2", listener)

        verify(exactly = 1) {
            loader("arg1")
            loader("arg2")
        }
    }

    @Test
    fun addListenerDeliversLoadResultToListener() {
    val subject = createSubject()
        every { loader(any()) } returns "result"

        subject.addListeners("arg", listener)

        verifySequence {
            listener(Pending())
            listener(Success("result"))
        }
}


    @Test
    fun addListenersWithNullValueFromLoadedDeliversEmptyResultToListener(){
        val subject = createSubject()
        every { loader(any()) } returns  null

        subject.addListeners("arg", listener)

        verifySequence {
            listener(Pending())
            listener(Empty())
        }
    }

    @Test
    fun addListenerWithFailedLoaderDeliversErrorResultToListener(){
        val expectedException = IllegalStateException()
        val subject = createSubject()
        every { loader(any()) } throws expectedException

        subject.addListeners("arg", listener)

        verifySequence {
            listener(Pending())
            listener(Error(expectedException))
        }
    }

    @Test
    fun addListenerDeliversCurrentResultsImmediately(){
        val firsListener = mockk<ValueListener<String>>(relaxed = true)
        val subject = createSubject()
        every { loader(any()) } returns "result"

        subject.addListeners("arg" , firsListener)
        subject.addListeners("arg", listener)

        verify (exactly = 1){
            listener(Success("result"))
        }
        confirmVerified(listener)
    }

    @Test
    fun addListenerDeliversPendingResultForNonFinishedLoad(){
        val awaitLoaderExecutionStart = CountDownLatch(1)
        val awaitLoaderExecutionFinish = CountDownLatch(1)
        every { loader(any()) } answers {
            /*awaitLoaderExecutionStart.countDown()*/
            awaitLoaderExecutionFinish.await(/*1, TimeUnit.SECONDS*/)
            "result"
        }
        val subject = createSubject(
            loaderExecutor = Executors.newSingleThreadExecutor()
        )

        subject.addListeners("arg", listener)
        awaitLoaderExecutionStart.await(1, TimeUnit.SECONDS)


        verifySequence {
            listener(Pending())
        }
        awaitLoaderExecutionFinish.countDown()
        verify(exactly = 1) {
            listener(Success("result"))
        }
        confirmVerified(listener)
    }

     @Test
     fun removeListenerCancelsLoadingAfterRemovingTheLastListeners(){
         val awaitLoaderExecutionStart = CountDownLatch(1)
         val awaitLoaderExecutionFinish = CountDownLatch(1)
         every { loader(any()) } answers {
             awaitLoaderExecutionStart.countDown()
             awaitLoaderExecutionFinish.await(1, TimeUnit.SECONDS)
             "result"
         }
         val subject = createSubject(
             loaderExecutor = Executors.newSingleThreadExecutor()
         )

         subject.addListeners("arg", listener)
         awaitLoaderExecutionStart.await(1, TimeUnit.SECONDS)
         subject.removeListener("arg", listener)
         awaitLoaderExecutionFinish.countDown()

         verifySequence {
             listener(Pending())
         }
     }

    @Test
    fun removeListenersStopsDeliverResultsViaReloadToListeners() {
        val subject = createSubject()
        every { loader(any()) } returns "result1" andThen "result1-update"

        subject.addListeners("arg", listener)
        subject.removeListener("arg", listener)
        subject.reloadAll()

        verifySequence {
            listener(Pending())
            listener(Success("result1"))
        }
    }

    @Test
    fun removeListenersStopsDeliveringResultsViaReloadArgumentToListener(){
        val subject = createSubject()
        every { loader(any()) } returns "result1" andThen "result1-update"

        subject.addListeners("arg", listener)
        subject.removeListener("arg", listener)
        subject.reloadArguments("arg")

        verifySequence {
            listener(Pending())
            listener(Success("result1"))
        }
    }
    @Test
    fun removeListenersStopsDeliveringResultsViaUpdateAllValues(){
        val subject = createSubject()
        every { loader(any()) } returns "result1"

        subject.addListeners("arg", listener)
        subject.removeListener("arg", listener)
        subject.updateAllValues("result1-update")

        verifySequence {
            listener(Pending())
            listener(Success("result1"))
        }
    }

    @Test
    fun reloadAllTriggersReloadingForAllActiveArguments(){
        val subject = createSubject()
        val listener1: ValueListener<String> = mockk(relaxed = true)
        val listener2: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg1") } returns "result1" andThen "result1-updated"
        every { loader("arg2") } returns "result2" andThen "result2-updated"

        subject.addListeners("arg1", listener1)
        subject.addListeners("arg2", listener2)
        subject.reloadAll()

        verifySequence {
            listener1(Pending())
            listener1(Success("result1"))
            listener1(Pending())
            listener1(Success("result1-updated"))
        }

        verifySequence {
            listener2(Pending())
            listener2(Success("result2"))
            listener2(Pending())
            listener2(Success("result2-updated"))
        }
    }

    @Test
    fun reloadAllWithSilentModeTriggersReloadingForAllActiveArgumentWithoutEmittingPendingStatus(){
        val subject = createSubject()
        val listener1: ValueListener<String> = mockk(relaxed = true)
        val listener2: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg1") } returns "result1" andThen "result1-update"
        every { loader("arg2") } returns "result2" andThen "result2-update"

        subject.addListeners("arg1", listener1)
        subject.addListeners("arg2", listener2)
        subject.reloadAll(silentMode = true)

        verifySequence {
            listener1(Pending())
            listener1(Success("result1"))
            listener1(Success("result1-update"))
        }

        verifySequence {
            listener2(Pending())
            listener2(Success("result2"))
            listener2(Success("result2-update"))
        }

        verify (exactly = 4){
            loader(any())
        }

    }

    @Test
    fun reloadedArgumentsDoesNothingForNonExistingArguments(){
        val subject = createSubject()
        every { loader(any()) } returns "result" andThen "result-updated"

        subject.addListeners("arg1", listener)
        subject.reloadArguments("other-arg")

        verifySequence {
            listener(Pending())
            listener(Success("result"))
        }
        verify (exactly = 1){
            loader(any())
        }
    }

    @Test
    fun reloadedArgumentSpecificArgument() {
        val subject = createSubject()
        val listener1: ValueListener<String> = mockk(relaxed = true)
        val listener2: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg1") } returns "result1" andThen "result1-updated"
        every { loader("arg2") } returns "result2" andThen "result2-updated"

        subject.addListeners("arg1", listener1)
        subject.addListeners("arg2", listener2)
        subject.reloadArguments("arg1")

        verifySequence {
            listener1(Pending())
            listener1(Success("result1"))
            listener1(Pending())
            listener1(Success("result1-updated"))
        }
        verifySequence {
            listener2(Pending())
            listener2(Success("result2"))
        }
        verify (exactly = 3){
            loader(any())
        }
    }
    @Test
    fun reloadedArgumentWithSilentModeReloadSpecificArgumentWithoutEmittingPendingStatus() {
        val subject = createSubject()
        val listener1: ValueListener<String> = mockk(relaxed = true)
        val listener2: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg1") } returns "result1" andThen "result1-updated"
        every { loader("arg2") } returns "result2" andThen "result2-updated"

        subject.addListeners("arg1", listener1)
        subject.addListeners("arg2", listener2)
        subject.reloadArguments("arg1", silentMode = true)

        verifySequence {
            listener1(Pending())
            listener1(Success("result1"))
            listener1(Success("result1-updated"))
        }
        verifySequence {
            listener2(Pending())
            listener2(Success("result2"))

        }
        verify (exactly = 3){
            loader(any())
        }
    }


    @Test
    fun updateAllValuesUpdatesAllResultsImmediately(){
        val subject = createSubject()
        every { loader(any()) } returns "result"

        subject.addListeners("arg", listener)
        subject.updateAllValues("new-result")

        verifyOrder {
            listener(Success("result"))
            listener(Success("new-result"))
        }
        verify (exactly = 1){
            loader(any())
        }
    }

    @Test
    fun updateAllValuesWithNullChangesAllResultToEmpty(){
        val subject = createSubject()
        every { loader(any()) } returns "result"

        subject.addListeners("arg", listener)
        subject.updateAllValues(null)

        verifyOrder {
            listener(Success("result"))
            listener(Empty())
        }

    }



    private fun createSubject( loaderExecutor: ExecutorService = immediateExecutorService()
    ) = LazyListenerSubject(
        loaderExecutor = loaderExecutor,
        handleExecutor = immediateExecutorService(),
        loader = loader
    )

}