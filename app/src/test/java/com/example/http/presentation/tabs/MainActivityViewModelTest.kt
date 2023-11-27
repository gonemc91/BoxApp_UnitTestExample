package com.example.http.presentation.tabs

import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.accounts.entities.Account
import com.example.http.presentation.main.MainActivityViewModel
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.createAccount
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityViewModelTest(): ViewModelTest() {




    @Test
    fun mainViewModelSharedUsernameOfCurrentUser(){
        val account = createAccount(username = "username")
        every { accountsRepository.getAccount() } returns flowOf(Success(account))
        val viewModel = MainActivityViewModel(accountsRepository)

        val username = viewModel.username.value

        assertEquals("@username",username)
    }

    @Test
    fun mainModelSharedEmptyStringIfCurrentUserUnavailable(){
        every { accountsRepository.getAccount() } returns flowOf(Pending())
        val viewModel = MainActivityViewModel(accountsRepository)

        val username = viewModel.username.value

        assertEquals("", username)
    }

    @Test
    fun mainViewModelListenerForFutureUsernameUpdates(){
        val flow: MutableStateFlow<Result<Account>> =
            MutableStateFlow(Success(createAccount(username = "username1")))
        every { accountsRepository.getAccount() } returns flow
        val viewModel = MainActivityViewModel(accountsRepository)

        val username1 = viewModel.username.value
        flow.value = Pending()
        val username2 = viewModel.username.value
        flow.value = Success(createAccount(username = "username2"))
        val username3 = viewModel.username.value

        assertEquals("@username1", username1)
        assertEquals("", username2)
        assertEquals("@username2", username3)
    }



}