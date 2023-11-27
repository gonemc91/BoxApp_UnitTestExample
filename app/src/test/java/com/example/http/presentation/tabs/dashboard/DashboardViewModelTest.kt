package com.example.http.presentation.tabs.dashboard

import android.graphics.Color
import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.main.tabs.dashboard.DashboardViewModel
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.createBoxAndSettings
import com.example.http.utils.requireValue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DashboardViewModelTest: ViewModelTest() {

    @MockK
    lateinit var boxesRepository: BoxesRepository

    private lateinit var boxesFlow: MutableStateFlow<Result<List<BoxAndSettings>>>
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp(){
        boxesFlow = MutableStateFlow(Pending())
        every {boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)} returns boxesFlow
        viewModel = DashboardViewModel(boxesRepository, accountsRepository, logger)
    }

    @Test
    fun reloadReloadOnlyActiveData(){
         every { boxesRepository.reload(any()) } just runs

        viewModel.reload()

        verify(exactly = 1) {
            boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)
        }
    }

    @Test
    fun boxesReturnsDataFromRepository(){
        val expectedList1 = listOf(
            createBoxAndSettings(id = 2, name = "Red", value = Color.RED),
            createBoxAndSettings(id = 3, name = "Green", value = Color.GREEN)
        )
        val expectedList2 = listOf(
            createBoxAndSettings(id = 3, name = "Yellow", value = Color.GREEN),
            createBoxAndSettings(id = 4, name = "Blue", value = Color.BLUE)
        )

        boxesFlow.value = Pending()
        val result1 = viewModel.boxes.requireValue()
        boxesFlow.value = Success(expectedList1)
        val result2 = viewModel.boxes.requireValue()
        boxesFlow.value = Success(expectedList2)
        val result3 = viewModel.boxes.requireValue()

        assertEquals(Pending<List<Box>>(), result1)
        assertEquals(expectedList1.map {it.box}, result2.getValueOrNull())
        assertEquals(expectedList2.map {it.box}, result3.getValueOrNull())
        verify(exactly = 1){
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
        }

    }



}