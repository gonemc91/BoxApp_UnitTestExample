package com.example.http.presentation.base

import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.arranged
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.Test


class BaseViewModelTest : ViewModelTest() {

    // Embedding parameters in BaseViewModel from VieModelTest via inheritance
    @InjectMockKs
    lateinit var viewModel: BaseViewModel

    @Test
    fun logoutCallsLogout() {
        arranged()

        viewModel.logout()

        verify(exactly = 1) {
            accountsRepository.logout()
        }
    }

    @Test
    fun logErrorLogsError(){
        val exception = IllegalStateException()

        viewModel.logError(exception)

        verify(exactly = 1) {
            logger.error(any(), refEq(exception))
        }
    }



}