package com.example.http.app.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.model.EmptyFieldException
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.MutableUnitLiveEvent
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val accountsSources: AccountsSources
) : ViewModel() {

    private val _initialUsernameEvent = MutableLiveEvent<String>()
    val initialUsernameEvent = _initialUsernameEvent.share()

    private val _saveInProgress = MutableLiveData(false)
    val saveInProgress = _saveInProgress.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _showEmptyFieldErrorEvent = MutableUnitLiveEvent()
    val showEmptyFieldErrorEvent = _showEmptyFieldErrorEvent.share()

    init {
        viewModelScope.launch {
            val account = accountsSources.getAccount()
                .filterNotNull()
                .first()
            _initialUsernameEvent.publishEvent(account.userName)
        }
    }

    fun saveUsername(newUsername: String) {
        viewModelScope.launch {
            showProgress()
            try {
                accountsSources.updateAccountUsername(newUsername)
                goBack()
            } catch (e: EmptyFieldException) {
                hideProgress()
                showEmptyFieldErrorMessage()
            }
        }
    }

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _saveInProgress.value = true
    }

    private fun hideProgress() {
        _saveInProgress.value = false
    }

    private fun showEmptyFieldErrorMessage() = _showEmptyFieldErrorEvent.publishEvent()


}