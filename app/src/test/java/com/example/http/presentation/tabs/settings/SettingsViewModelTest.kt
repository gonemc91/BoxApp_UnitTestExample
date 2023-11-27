package com.example.http.presentation.tabs.settings

import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.base.ViewModelExceptionTest
import com.example.http.presentation.main.tabs.settings.SettingsViewModel
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.arranged
import com.example.http.testutils.createBox
import com.example.http.testutils.createBoxAndSettings
import com.example.http.utils.requireValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest: ViewModelTest() {

    @RelaxedMockK
    private lateinit var boxesRepository: BoxesRepository

    private lateinit var boxesFlow: MutableStateFlow<Result<List<BoxAndSettings>>>

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        boxesFlow = MutableStateFlow(Pending())
        boxesRepository = createBoxesRepository(boxesFlow)



        viewModel = SettingsViewModel(boxesRepository, accountsRepository, logger)
    }


    @Test
    fun boxSettingsReturnDataFromRepository() {
        val expectedBoxes1 = Pending<List<BoxAndSettings>>()
        val expectedBoxes2 = listOf(
            createBoxAndSettings(id = 1, name = "Box1", isActive = true),
            createBoxAndSettings(id = 2, name = "Box2", isActive = true)
        )
        val expectedBoxes3 = listOf(
            createBoxAndSettings(id = 2, name = "Box2", isActive = false),
            createBoxAndSettings(id = 3, name = "Box3", isActive = false)
        )

        boxesFlow.value = Pending()
        val result1 = viewModel.boxSettings.requireValue()
        boxesFlow.value = Success(expectedBoxes2)
        val result2 = viewModel.boxSettings.requireValue()
        boxesFlow.value = Success(expectedBoxes3)
        val result3 = viewModel.boxSettings.requireValue()

        assertEquals(expectedBoxes1, result1)
        assertEquals(expectedBoxes2, result2.getValueOrNull())
        assertEquals(expectedBoxes3, result3.getValueOrNull())
        verify(exactly = 1) {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ALL)
        }

    }

    @Test
    fun enableBoxEnablesBox() {
        val box = createBox()

        viewModel.enableBox(box)

        coVerify(exactly = 1) {
            boxesRepository.activateBox(box)
        }
    }

    @Test
    fun disableBoxDisableBox() {
        val box = createBox()

        viewModel.disableBox(box)

        coVerify(exactly = 1) {
            boxesRepository.deactivateBox(box)
        }
    }

    @Test
    fun tryAgainReloadData() {
        arranged()

        viewModel.tryAgain()

        coVerify(exactly = 1) {
            boxesRepository.reload(BoxesFilter.ALL)
        }
    }

    abstract class SettingsViewModelExceptionTest : ViewModelExceptionTest<SettingsViewModel>() {

        lateinit var boxesRepository: BoxesRepository

        override lateinit var viewModel: SettingsViewModel


        @Before
        fun setUp() {
                boxesRepository = createBoxesRepository(flowOf())
                viewModel = SettingsViewModel(boxesRepository, accountsRepository, logger)
        }

        class EnabledBoxExceptionsTest : SettingsViewModelExceptionTest() {
            override fun arrangeWithException(e: Exception) {
                coEvery { boxesRepository.activateBox(any()) } throws e
            }

            override fun act() {
                viewModel.enableBox(createBox())
            }

        }

        class DisabledBoxExceptionTest: SettingsViewModelExceptionTest(){
            override fun arrangeWithException(e: Exception) {
                coEvery { boxesRepository.deactivateBox(any()) } throws e
            }

            override fun act() {
                viewModel.disableBox(createBox())
            }
        }


        class TryAgainExceptionTest: SettingsViewModelExceptionTest(){
            override fun arrangeWithException(e: Exception) {
                coEvery { boxesRepository.reload(BoxesFilter.ALL) } throws e
            }

            override fun act() {
                viewModel.tryAgain()
            }
        }

    }

        private companion object {
            fun createBoxesRepository(flow: Flow<Result<List<BoxAndSettings>>>): BoxesRepository {
                val repository = mockk<BoxesRepository>(relaxed = true)
                every { repository.getBoxesAndSettings(any()) } returns flow
                return repository
            }
        }

    }
