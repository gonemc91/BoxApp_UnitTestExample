package com.example.http.app.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountsSources
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
        viewModelScope.launch {
            accountRepository.logout()
            restartAppFromLoginScreen()
        }
    }

    private fun restartAppFromLoginScreen() {
        _restartFromLoginEvent.publishEvent()
    }


}