package com.example.nav_components_2_tabs_exercise.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.utils.MutableLiveEvent
import com.example.nav_components_2_tabs_exercise.utils.publishEvent
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountsRepository
) : ViewModel() {
    private val _account = MutableLiveData<Account>()
    val account = _account.share()


    private val _restartFromLoginEvent = MutableLiveEvent<Unit>()
    val restartWithSignInEvent = _restartFromLoginEvent.share()


    init {
        viewModelScope.launch {
            accountRepository.getAccount().collect {
                _account.value = it
            }
        }
    }

    fun logout() {
        // now logout is not async, so simply call it and restart the app from login screen
        accountRepository.logout()
        restartAppFromLoginScreen()
    }

    private fun restartAppFromLoginScreen() {
        _restartFromLoginEvent.publishEvent()
    }


}