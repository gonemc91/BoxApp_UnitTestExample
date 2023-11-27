package com.example.http.presentation.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.domain.Result
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.accounts.entities.Account
import com.example.http.presentation.base.BaseViewModel
import com.example.http.utils.logger.Logger
import com.example.http.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    accountRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountRepository, logger) {

    private val _account = MutableLiveData<Result<Account>>()
    val account = _account.share()


    init {
        viewModelScope.safeLaunch {
            accountRepository.getAccount().collect {
                _account.value = it
            }
        }
    }

    fun reload() {
        accountsRepository.reloadAccount()
        }
    }



