package com.example.http.app.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.Singletons
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



/**
 * SplashViewModel checks whether user is signed-in or not.
 */
class SplashViewModel(
    private val accountsRepository: AccountsRepository = Singletons.accountsRepository
) : ViewModel() {

    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()

    init {
        viewModelScope.launch {
            delay(2000)
            _launchMainScreenEvent.publishEvent(accountsRepository.isSignedIn())
        }
    }
}