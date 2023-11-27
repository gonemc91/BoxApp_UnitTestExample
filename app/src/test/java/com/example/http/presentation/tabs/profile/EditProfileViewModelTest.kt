package com.example.http.presentation.tabs.profile

import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Field
import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.accounts.entities.Account
import com.example.http.presentation.base.ViewModelExceptionTest
import com.example.http.presentation.main.tabs.profile.EditProfileViewModel
import com.example.http.testutils.CoroutineSubject
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.arranged
import com.example.http.testutils.createAccount
import com.example.http.testutils.returnsSubject
import com.example.http.utils.requireValue
import com.example.nav_components_2_tabs_exercise.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditProfileViewModelTest: ViewModelTest() {

    private lateinit var flow: MutableStateFlow<Result<Account>>

    private lateinit var vieModel: EditProfileViewModel

    @Before
    fun setUp(){
        flow = MutableStateFlow(Pending())
        every { accountsRepository.getAccount() } returns flow
        vieModel = EditProfileViewModel(accountsRepository, logger)
    }


    @Test
    fun saveUsernameShowsProgress(){
        // It will be executed until it is completed in the test
        coEvery { accountsRepository.updateAccountUsername(any())}  returnsSubject
                CoroutineSubject()

        vieModel.saveUsername("username")

        assertTrue(vieModel.saveInProgress.requireValue())
    }

    @Test
    fun saveUsernameSendsUsernameToRepository(){
        coEvery { accountsRepository.updateAccountUsername(any()) } just runs

        vieModel.saveUsername("username")

        coVerify (exactly = 1){
            accountsRepository.updateAccountUsername("username")
        }
    }

    @Test
    fun saveUsernameWithSuccessHidesProgressAndGoesBack(){
        coEvery { accountsRepository.updateAccountUsername(any()) } just runs

        vieModel.saveUsername("username")

        assertFalse(vieModel.saveInProgress.requireValue())
        assertNotNull(vieModel.goBackEvent.requireValue().get())
    }

    @Test
    fun saveUsernameWithErrorHidesProgress(){
        coEvery { accountsRepository.updateAccountUsername(any()) } throws
                IllegalStateException()

        vieModel.saveUsername("username")

        assertFalse(vieModel.saveInProgress.requireValue())
        assertNull(vieModel.goBackEvent.value?.get())
    }
    @Test
    fun saveUsernameWithEmptyValueShowsError(){
        coEvery { accountsRepository.updateAccountUsername(any()) } throws
                EmptyFieldException(Field.Username)

        vieModel.saveUsername("username")

        assertEquals(
            R.string.field_is_empty,
            vieModel.showErrorMessageResEvent.requireValue().get()
        )
    }

    @Test
    fun initialUsernameEventReturnsFirstValueFromRepository() {
        arranged()

        flow.value = Success(createAccount(username = "username1"))
        val value1 = vieModel.initialUsernameEvent.value?.get()
        flow.value = Success(createAccount(username = "username2"))
        val value2 = vieModel.initialUsernameEvent.value?.get()

        assertEquals("username1", value1)
        assertNull(value2)
    }

    class SaveUsernameExceptionTest : ViewModelExceptionTest<EditProfileViewModel>() {

        override lateinit var viewModel: EditProfileViewModel

        @Before
        fun setUp() {
            every { accountsRepository.getAccount() } returns
                    flowOf(Success(createAccount()))
            viewModel = EditProfileViewModel(accountsRepository, logger)
        }

        override fun arrangeWithException(e: Exception) {
            coEvery { accountsRepository.updateAccountUsername(any()) } throws e
        }

        override fun act() {
            viewModel.saveUsername("username")
        }
    }




}