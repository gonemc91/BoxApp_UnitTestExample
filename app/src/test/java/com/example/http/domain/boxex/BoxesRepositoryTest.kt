package com.example.http.domain.boxex

import com.example.http.domain.AuthException
import com.example.http.domain.BackendException
import com.example.http.domain.Empty
import com.example.http.domain.Error
import com.example.http.domain.Pending
import com.example.http.domain.Success
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.BoxesSource
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.domain.wrapBackendExceptions
import com.example.http.testutils.catch
import com.example.http.testutils.createAccount
import com.example.http.testutils.createBox
import com.example.http.testutils.createBoxAndSettings
import com.example.http.testutils.wellDone
import com.example.http.utils.async.LazyFlowFactory
import com.example.http.utils.async.LazyFlowSubject
import com.example.http.utils.async.SuspendValueLoader
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BoxesRepositoryTest {

    @get: Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var accountsRepository: AccountsRepository

    @MockK
    lateinit var boxesSource: BoxesSource

    @MockK
    lateinit var lazyFlowFactory: LazyFlowFactory

    @MockK
    lateinit var lazyFlowSubject: LazyFlowSubject<BoxesFilter, List<BoxAndSettings>>

    lateinit var boxesRepository: BoxesRepository


    @Before
    fun setUp(){
        every { lazyFlowFactory.createLazyFlowSubject<BoxesFilter, List<BoxAndSettings>>(any())
        } returns lazyFlowSubject

        boxesRepository = BoxesRepository(
            accountsRepository,
            boxesSource,
            lazyFlowFactory
        )
        mockkStatic("com.example.http.domain.ExceptionsKt")
    }

    @After
    fun tearDown(){
        unmockkStatic("com.example.http.domain.ExceptionsKt")
    }

    @Test
    fun initLoadBoxes() = runTest {
        val expectedListFromSource = listOf(
            createBoxAndSettings(id = 1, name = "Red"),
            createBoxAndSettings(id = 2, name = "Green")
        )
        coEvery {
            boxesSource.getBoxes(BoxesFilter.ONLY_ACTIVE)
        } returns expectedListFromSource
        val fetcherSlot = arrangeRepositoryWithLazyFlowSlot()

        val loader = fetcherSlot.captured
        val list = loader(BoxesFilter.ONLY_ACTIVE)

        assertEquals(expectedListFromSource, list)
    }

    @Test
    fun initDefaultBackendExceptionWrapper() = runTest {
        val expectedListFromSource = listOf(
            createBoxAndSettings(id = 1, name = "Red"),
            createBoxAndSettings(id = 2, name = "Green")
        )
        coEvery { wrapBackendExceptions<List<BoxAndSettings>>(any())
        }returns expectedListFromSource
        val fetchSlot = arrangeRepositoryWithLazyFlowSlot()

        val loader = fetchSlot.captured
        val list = loader(BoxesFilter.ONLY_ACTIVE)

        assertEquals(expectedListFromSource, list)
    }
    @Test
    fun getBoxesWithSuccessAccountListenBoxes() = runTest {
        val expectedBoxes = listOf(
            createBoxAndSettings(id = 1), createBoxAndSettings(id = 2)
        )
        every { accountsRepository.getAccount() } returns
                flowOf(Success(createAccount(id = 1)))
        coEvery { lazyFlowSubject.listen(any()) } returns
                flowOf(Success(expectedBoxes))

        val collectedResults = boxesRepository
            .getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        assertEquals(1, collectedResults.size)
        val boxes = collectedResults.first()
        assertEquals(expectedBoxes, boxes.getValueOrNull())
    }

    @Test
    fun getBoxesWithoutSuccessAccountMapsAccountResult() = runTest {
        val exception = IllegalStateException()
        every { accountsRepository.getAccount() } returns
                flowOf(
                    Error(exception),
                    Empty(),
                    Pending()
                )
        val  collectedResults = boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        assertEquals(3, collectedResults.size)
        assertEquals(exception, (collectedResults[0] as Error).error)
        assertTrue(collectedResults[1] is Empty)
        assertTrue(collectedResults[2] is Pending)
    }

    @Test
    fun getBoxesListensForFurtherAccountChanges() = runTest {
        val boxesForAccount1 = listOf(
            createBoxAndSettings(id = 1)
        )
        val boxesForAccount2 = listOf(
            createBoxAndSettings(id = 2),
            createBoxAndSettings(id = 3),
            )
        every { lazyFlowSubject.listen(any()) } returns
                flowOf(Pending(), Success(boxesForAccount1)) andThen
                flowOf(Pending(), Success(boxesForAccount2))
        every { accountsRepository.getAccount() } returns flowOf(
            Success(createAccount(id = 1)),
            Success(createAccount(id = 1))
        )

        val collectingResults = boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        assertEquals(4, collectingResults.size)
        assertTrue(collectingResults[0] is Pending)
        assertEquals(boxesForAccount1, collectingResults[1].getValueOrNull())
        assertTrue(collectingResults[2] is Pending)
        assertEquals(boxesForAccount2, collectingResults[3].getValueOrNull())
    }

    @Test
    fun reloadWithFailedAccountResultReloadsAccountData() = runTest {
        every { accountsRepository.getAccount() } returns
                flowOf(Error(IllegalStateException()))
        every { accountsRepository.reloadAccount() } just runs

        boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).collect()
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)

        verify(exactly = 1) {
            accountsRepository.reloadAccount()
        }
    }

    @Test
    fun reloadWithAnyOtherAccountResultReloadsBoxesFlow() = runTest {
        every { accountsRepository.getAccount() } returns
                flowOf(Success(createAccount(id = 1)))
        every { lazyFlowSubject.listen(any()) } returns flowOf()
        every { lazyFlowSubject.reloadArguments(any(), any()) } just runs

        boxesRepository.getBoxesAndSettings(BoxesFilter.ALL).collect()
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)

        verify(exactly = 1) {
            lazyFlowSubject.reloadArguments(BoxesFilter.ONLY_ACTIVE)
        }
    }

    @Test
    fun activateBoxActivatesBoxAndReloadBoxesFlow() = runTest {
        val box = createBox(id =7 )
        every { lazyFlowSubject.reloadAll(any())} just runs
        coEvery { boxesSource.setIsActive(any(), any()) } just runs

        boxesRepository.activateBox(box)

        coVerifyOrder {
            boxesSource.setIsActive(box.id, true)
            lazyFlowSubject.reloadAll(silentMode = true)
        }

    }

    @Test
    fun activateBoxWith401ErrorThrowsAuthException() = runTest {
        coEvery { boxesSource.setIsActive(any(), any()) } throws
                BackendException(code = 401, message = "Oops")

        catch<AuthException> { boxesRepository.activateBox(createBox())}

        wellDone()
    }

    @Test
    fun deactivateBoxDeactivateBoxAndReloadBoxesFlow() = runTest {
        val box = createBox(id = 7)
        every { lazyFlowSubject.reloadAll(any()) } just runs
        coEvery { boxesSource.setIsActive(any(), any()) } just runs

        boxesRepository.deactivateBox(box)

        coVerifyOrder {
            boxesSource.setIsActive(box.id, false)
            lazyFlowSubject.reloadAll(silentMode = true)
        }
    }

    @Test
    fun deactivateBoxWith401ErrorThrowsAuthException() = runTest {
        coEvery { boxesSource.setIsActive(any(),any()) } throws
                BackendException(code = 401, message = "Oops")

        catch<AuthException> {boxesRepository.deactivateBox(createBox()) }

        wellDone()
    }


    private fun arrangeRepositoryWithLazyFlowSlot(): CapturingSlot<SuspendValueLoader<BoxesFilter, List<BoxAndSettings>>>{
        val factory: LazyFlowFactory = mockk()
        val slot =
            slot<SuspendValueLoader<BoxesFilter, List<BoxAndSettings>>>()
        every { factory.createLazyFlowSubject(capture(slot)) } returns mockk()
        BoxesRepository(accountsRepository, boxesSource, factory)
        return slot
    }







}