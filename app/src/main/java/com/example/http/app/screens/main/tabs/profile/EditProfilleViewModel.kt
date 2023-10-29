package com.example.http.app.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.app.Success
import com.example.http.app.model.EmptyFieldException
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.screens.base.BaseViewModel
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.MutableUnitLiveEvent
import com.example.http.app.utils.logger.Logger
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import com.example.nav_components_2_tabs_exercise.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    logger: Logger) : BaseViewModel(accountsRepository, logger) {

    private val _initialUsernameEvent = MutableLiveEvent<String>()
    val initialUsernameEvent = _initialUsernameEvent.share()

    private val _saveInProgress = MutableLiveData(false)
    val saveInProgress = _saveInProgress.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _showErrorEvent = MutableLiveEvent<Int>()
    val showErrorEvent = _showErrorEvent.share()

    init {

        viewModelScope.launch {
            val res = accountsRepository.getAccount()
                .filterNotNull()
                .first()
           if (res is Success)_initialUsernameEvent.publishEvent(res.value.userName)
        }
    }

    fun saveUsername(newUsername: String) = viewModelScope.safeLaunch {
        showProgress()
            try {
                accountsRepository.updateAccountUsername(newUsername)
                goBack()
            } catch (e: EmptyFieldException) {
                showEmptyFieldErrorMessage()
            }finally {
                hideProgress()
            }
        }


    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _saveInProgress.value = true
    }

    private fun hideProgress() {
        _saveInProgress.value = false
    }

    private fun showEmptyFieldErrorMessage() = _showErrorEvent.publishEvent(R.string.field_is_empty)


}