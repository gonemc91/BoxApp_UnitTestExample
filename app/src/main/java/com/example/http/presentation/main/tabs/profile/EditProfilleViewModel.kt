package com.example.http.presentation.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Success
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.presentation.base.BaseViewModel
import com.example.http.utils.MutableLiveEvent
import com.example.http.utils.MutableUnitLiveEvent
import com.example.http.utils.logger.Logger
import com.example.http.utils.publishEvent
import com.example.http.utils.share
import com.example.nav_components_2_tabs_exercise.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _initialUsernameEvent = MutableLiveEvent<String>()
    val initialUsernameEvent = _initialUsernameEvent.share()

    private val _saveInProgress = MutableLiveData(false)
    val saveInProgress = _saveInProgress.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    init {
        viewModelScope.safeLaunch {
            val res = accountsRepository.getAccount()
                //discarding all State except Success and Error
                .filter { it.isFinished() }
                //get first value
                .first()
           if (res is Success)_initialUsernameEvent.publishEvent(res.value.username)
        }
    }

    fun saveUsername(newUsername: String) = viewModelScope.safeLaunch {
        showProgress()
            try {
                accountsRepository.updateAccountUsername(newUsername)
                goBack()
            } catch (e: EmptyFieldException) {
                showErrorMessage(R.string.field_is_empty)
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



}