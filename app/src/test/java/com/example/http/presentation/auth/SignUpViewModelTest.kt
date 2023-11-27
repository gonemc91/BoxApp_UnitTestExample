package com.example.http.presentation.auth

import com.example.http.domain.AccountAlreadyExistsException
import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Field
import com.example.http.domain.PasswordMismatchException
import com.example.http.presentation.base.ViewModelExceptionTest
import com.example.http.presentation.main.auth.SignUpViewModel
import com.example.http.testutils.CoroutineSubject
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.createSignUpData
import com.example.http.testutils.returnsSubject
import com.example.http.utils.requireValue
import com.example.nav_components_2_tabs_exercise.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.runs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpViewModelTest: ViewModelTest()  {

    @InjectMockKs
    private lateinit var viewModel: SignUpViewModel

    @Test
    fun testInitialState() {
        val expectedState = createInitialState()

        val state = viewModel.state.requireValue()

        assertEquals(expectedState, state)
    }

    @Test
    fun signUpShowsProgress(){
        val expectedState = createInitialState(
            signUpInProgress = true
        )
        coEvery { accountsRepository.signUp(any())} returnsSubject CoroutineSubject()

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }


    @Test
    fun signUpSendsDataToRepository(){
        val expectedData = createSignUpData()
        coEvery {accountsRepository.signUp(any()) } just runs

        viewModel.signUp(expectedData)

       coVerify(exactly = 1) {
           accountsRepository.signUp(expectedData)
       }
    }


    @Test
    fun signUpWithSuccessHideProgress() {
        val signUpData = createSignUpData()
        val subject = CoroutineSubject<Unit>()
        coEvery { accountsRepository.signUp(signUpData) } returnsSubject
                subject

        viewModel.signUp(signUpData)

        assertTrue(viewModel.state.value!!.signUpInProgress)
        subject.sendSuccess(Unit)
        assertFalse(viewModel.state.value!!.signUpInProgress)
    }

    @Test
    fun signUpWithExceptionHideProgress(){
        coEvery { accountsRepository.signUp(any()) } throws
                IllegalStateException("Oops")

        viewModel.signUp(createSignUpData())

        assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signUpWithEmptyEmailExceptionShowsError(){
        val expectedState = createInitialState(
            emailErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws
                EmptyFieldException(Field.Email)

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithEmptyUsernameExceptionShowsError(){
        val expectedState = createInitialState(
            usernameErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws
                EmptyFieldException(Field.Username)

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithEmptyPasswordExceptionShowsError(){
        val expectedState = createInitialState(
            passwordErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws
                EmptyFieldException(Field.Password)

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }


    @Test
    fun signUpWithPasswordMismatchExceptionShowsError(){
        val expectedState = createInitialState(
            repeatPasswordErrorMessageRes  = R.string.password_mismatch
        )
        coEvery { accountsRepository.signUp(any()) } throws
                PasswordMismatchException()

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithAccountAlreadyExistsException(){
        val expectedState = createInitialState(
            emailErrorMessageRes  =  R.string.account_already_exists
        )
        coEvery { accountsRepository.signUp(any()) } throws
                AccountAlreadyExistsException(Exception("409"))

        viewModel.signUp(createSignUpData())

        assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun stateShowProgressWithPendingOperationReturnsTrue(){
        val state = createInitialState(signUpInProgress = true)

        val showProgress = state.showProgress

        assertTrue(showProgress)
    }

    @Test
    fun stateEnableViewsPendingOperationReturnsFalse(){
        val state = createInitialState(signUpInProgress = true)

        val enableView = state.enableViews

        assertFalse(enableView)
    }

    @Test
    fun stateShowProgressWithoutPendingOperationReturnsFalse(){
        val state = createInitialState(signUpInProgress = false)

        val showProgress = state.showProgress

        assertFalse(showProgress)
    }

    @Test
    fun stateEnableViewsWithoutPendingOperationReturnsTrue(){
        val state = createInitialState(signUpInProgress = false)

        val enableViews = state.enableViews

        assertTrue(enableViews)
    }

  class SignUpExceptionTests: ViewModelExceptionTest<SignUpViewModel>(){
      @InjectMockKs
      override lateinit var viewModel: SignUpViewModel
      override fun arrangeWithException(e: Exception) {
         coEvery { accountsRepository.signUp(any()) } throws e
      }
      override fun act() {
          viewModel.signUp(createSignUpData())
      }
  }

    private fun createInitialState(
        emailErrorMessageRes: Int = SignUpViewModel.NO_ERROR_MESSAGE,
        passwordErrorMessageRes: Int =SignUpViewModel.NO_ERROR_MESSAGE,
        repeatPasswordErrorMessageRes: Int = SignUpViewModel.NO_ERROR_MESSAGE,
        usernameErrorMessageRes: Int = SignUpViewModel.NO_ERROR_MESSAGE,
        signUpInProgress: Boolean = false
    )= SignUpViewModel.State(
        emailErrorMessageRes = emailErrorMessageRes,
        passwordErrorMessageRes = passwordErrorMessageRes,
        repeatPasswordErrorMessageRes = repeatPasswordErrorMessageRes,
        usernameErrorMessageRes = usernameErrorMessageRes,
        signUpInProgress = signUpInProgress
    )


}