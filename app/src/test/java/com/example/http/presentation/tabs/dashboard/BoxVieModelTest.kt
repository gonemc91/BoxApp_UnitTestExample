package com.example.http.presentation.tabs.dashboard

import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.main.tabs.dashboard.BoxViewModel
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.createBoxAndSettings
import com.example.http.utils.requireValue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BoxVieModelTest:  ViewModelTest(){

    lateinit var flow: MutableStateFlow<Result<List<BoxAndSettings>>>
    @MockK
    lateinit var boxesRepository: BoxesRepository

    lateinit var viewModel: BoxViewModel

    private val boxId = 1L
    private val anotherBoxId = 2L

    @Before
    fun setUp(){
        flow = MutableStateFlow(Pending())
        every { boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
        } returns flow

        viewModel = BoxViewModel(boxId, boxesRepository, accountsRepository, logger)
    }


    @Test
    fun shouldExistsEventIsFiredAfterDisablingBox(){
        val listWithBox = listOf(
            createBoxAndSettings(id = boxId)
        )
        val listWithoutBox = listOf(
            createBoxAndSettings(id = anotherBoxId)
        )

        flow.value = Success(listWithBox)
        val shouldExitEvent1 = viewModel.shouldExitEvent.requireValue().get()!!
        flow.value = Success(listWithoutBox)
        val shouldExistEvent2 = viewModel.shouldExitEvent.requireValue().get()!!

        assertFalse(shouldExitEvent1)
        assertTrue(shouldExistEvent2)
    }


}