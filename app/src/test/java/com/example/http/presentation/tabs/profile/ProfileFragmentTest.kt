package com.example.http.presentation.tabs.profile

import com.example.http.domain.Pending
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.accounts.entities.Account
import com.example.http.presentation.main.tabs.profile.ProfileViewModel
import com.example.http.testutils.ViewModelTest
import com.example.http.testutils.arranged
import com.example.http.testutils.createAccount
import com.example.http.utils.requireValue
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest: ViewModelTest()  {

    private lateinit var flow: MutableStateFlow<Result<Account>>

    private lateinit var vieModel: ProfileViewModel

    @Before
    fun setUp(){
        flow = MutableStateFlow(Pending())
        every { accountsRepository.getAccount() } returns flow
        vieModel = ProfileViewModel(accountsRepository, logger)
    }

    @Test
    fun reloadReloadsAccount(){
        arranged()

        vieModel.reload()

        verify(exactly = 1) {
            accountsRepository.reloadAccount()
        }
    }

    @Test
    fun accountReturnsDataFromRepository(){
        val expectedAccount1 = Pending<Account>()
        val expectedAccount2 = createAccount(id = 2, username = "name2")
        val expectedAccount3 = createAccount(id = 3, username = "name3")

        flow.value = expectedAccount1
        val result1 = vieModel.account.requireValue()
        flow.value = Success(expectedAccount2)
        val result2 = vieModel.account.requireValue()
        flow.value = Success(expectedAccount3)
        val result3 = vieModel.account.requireValue()


        assertEquals(expectedAccount1, result1)
        assertEquals(expectedAccount2, result2.getValueOrNull())
        assertEquals(expectedAccount3, result3.getValueOrNull())
    }




}