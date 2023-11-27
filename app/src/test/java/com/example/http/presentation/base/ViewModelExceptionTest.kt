package com.example.http.presentation.base

import com.example.http.domain.AuthException
import com.example.http.domain.BackendException
import com.example.http.domain.ConnectionException
import com.example.http.testutils.ViewModelTest
import com.example.http.utils.requireValue
import com.example.nav_components_2_tabs_exercise.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
abstract class ViewModelExceptionTest<VM : BaseViewModel>: ViewModelTest() {

    abstract val viewModel: VM

    abstract fun arrangeWithException(e: Exception)

    abstract fun act()


    @Test
    fun safeLaunchWithConnectionExceptionShowMessage() {
        val exception = ConnectionException(Exception())
        arrangeWithException(exception)

        act()

        assertEquals(
            R.string.connection_error,
            viewModel.showErrorMessageResEvent.requireValue().get()
        )
    }
    @Test
    fun safeLaunchWithBackendExceptionShowMessage(){
        val exception = BackendException(404, "Some error message")
        arrangeWithException(exception)

        act()

        assertEquals(
            exception.message,
            viewModel.showErrorMessageEvent.requireValue().get()
        )
    }



    @Test
    fun safeLaunchWithAuthExceptionRestartsFromLoginScreen(){
        val exception = AuthException(Exception())
        arrangeWithException(exception)

        act()

        assertNotNull(
            viewModel.showAuthErrorAndRestartEvent.requireValue().get()
        )
    }

    @Test
    fun safeLaunchWithOtherExceptionsShowInternalErrorMessage(){
        val exception = IllegalStateException()
        arrangeWithException(exception)

        act()

        assertEquals(
            R.string.internal_error,
            viewModel.showErrorMessageResEvent.requireValue().get()
        )
    }



}