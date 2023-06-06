package com.example.nav_components_2_tabs_exercise.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.utils.MutableLiveEvent
import com.example.nav_components_2_tabs_exercise.utils.publishEvent
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.launch


/**
 * SplashViewModel checks whether user is signed-in or not.
 */

class SplashViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {
    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()


    init {
        viewModelScope.launch {
            _launchMainScreenEvent.publishEvent(accountsRepository.isSignedIn())
        }
    }
}