package com.example.http.presentation.auth

import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Field
import com.example.http.domain.InvalidCredentialsException
import com.example.http.presentation.base.ViewModelExceptionTest
import com.example.http.presentation.main.auth.SignInViewModel
import com.example.http.testutils.CoroutineSubject
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.returnsSubject
import com.example.http.utils.requireValue
import com.example.nav_components_2_tabs_exercise.R
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.runs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInViewModelTest: ViewModelTest() {

    @InjectMockKs
    lateinit var viewModel: SignInViewModel

    @Test
    fun testInitialState(){
        val expectedState = SignInViewModel.State(
            emptyEmailError = false,
            emptyPasswordError = false,
            signInInProgress = false
        )

        val state = viewModel.state.requireValue()


        assertEquals(expectedState, state)
    }

    @Test
    fun signInWithSuccessHidesProgress(){
        val subject = CoroutineSubject<Unit>()
        coEvery { accountsRepository.signIn(any(), any()) } returnsSubject subject

        viewModel.signIn("email", "password")

        assertTrue(viewModel.state.requireValue().showProgress)
        subject.sendSuccess(Unit)
        assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signInWithExceptionHidesProgress(){
        coEvery { accountsRepository.signIn(any(),any()) } throws
                IllegalStateException("Oops")

        viewModel.signIn("email", "password")

        assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signInWithEmptyEmailExceptionShowsError(){
        val expectedState = SignInViewModel.State(
            emptyEmailError = true,
            emptyPasswordError = false,
            signInInProgress = false
        )
        coEvery { accountsRepository.signIn(any(),any()) } throws
                EmptyFieldException(Field.Email)

        viewModel.signIn("    ", "password")


        assertEquals(expectedState,viewModel.state.requireValue())
    }

    @Test
    fun signInWithEmptyUsernameExceptionShowsError(){
        val expectedState = SignInViewModel.State(
            emptyEmailError = false,
            emptyPasswordError = true,
            signInInProgress = false
        )
        coEvery { accountsRepository.signIn(any(), any()) } throws
                EmptyFieldException(Field.Password)

        viewModel.signIn("email", "      ")

        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signInWithInvalidCredentialsExceptionShowsErrorAndClearsPasswordField() {
        val expectedState = SignInViewModel.State(
            emptyEmailError = false,
            emptyPasswordError = false,
            signInInProgress = false
        )
        coEvery { accountsRepository.signIn(any(), any()) } throws
               InvalidCredentialsException(Exception())

        viewModel.signIn("email", "password")

        assertNotNull(viewModel.clearPasswordEvent.requireValue().get())
        assertEquals(
            R.string.invalid_email_or_password,
            viewModel.showAuthToastEvent.requireValue().get()
        )
        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signedInSuccessLaunchesTabsScreen(){
        coEvery { accountsRepository.signIn(any(), any()) } just runs

        viewModel.signIn("username", "password")

        assertNotNull(viewModel.navigateToTabsEvent.requireValue().get())
    }

    class SignInExceptionTests: ViewModelExceptionTest<SignInViewModel>(){
        @InjectMockKs
        override lateinit var viewModel: SignInViewModel

        override fun arrangeWithException(e: Exception) {
            coEvery { accountsRepository.signIn(any(), any()) } throws e
        }

        override fun act() {
            viewModel.signIn("email", "password")
        }
    }





}