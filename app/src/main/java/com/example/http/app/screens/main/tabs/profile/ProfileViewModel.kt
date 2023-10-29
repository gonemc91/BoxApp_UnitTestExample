package com.example.http.app.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.app.Result
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.screens.base.BaseViewModel
import com.example.http.app.utils.logger.Logger
import com.example.http.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  accountRepository: AccountsRepository,
  logger: Logger
) : BaseViewModel(accountRepository, logger) {

    private val _account = MutableLiveData<Result<Account>>()
    val account = _account.share()


    init {
        viewModelScope.launch {
            accountRepository.getAccount().collect {
                _account.value = it
            }
        }
    }

    fun reload() {
        accountsRepository.reloadAccount()
        }
    }



