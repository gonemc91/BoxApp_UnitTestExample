package com.example.http.presentation.splash

import com.example.http.testutils.ViewModelTest
import com.example.http.utils.requireValue
import io.mockk.every
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashVIewModelTest: ViewModelTest() {


    @Test
    fun splashViewModelWithSignedInUserSendsLaunchMainScreenWithTrueValue() {
        every { accountsRepository.isSignedIn() } returns true

        val viewModel = SplashViewModel(accountsRepository)

        val isSignedIn = viewModel.launchMainScreenEvent.requireValue().get()!!
        assertTrue(isSignedIn)
    }

    @Test
    fun splashViewModelWithoutSignedInUserSendsLaunchMainScreenWithFalseValue(){
        every { accountsRepository.isSignedIn() } returns false

        val viewModel = SplashViewModel(accountsRepository)

        val isSignedIn = viewModel.launchMainScreenEvent.requireValue().get()!!
        assertFalse(isSignedIn)

    }
}