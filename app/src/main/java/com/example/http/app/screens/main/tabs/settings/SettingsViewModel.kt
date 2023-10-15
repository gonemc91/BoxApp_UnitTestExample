package com.example.http.app.screens.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.app.Result
import com.example.http.app.Singletons
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.model.boxes.BoxesRepository
import com.example.http.app.model.boxes.entities.Box
import com.example.http.app.model.boxes.entities.BoxAndSettings
import com.example.http.app.model.boxes.entities.BoxesFilter
import com.example.http.app.screens.base.BaseViewModel
import com.example.http.app.utils.logger.LogCatLogger
import com.example.http.app.utils.logger.Logger
import com.example.http.app.utils.share
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val boxesRepository: BoxesRepository = Singletons.boxesRepository,
    accountsRepository: AccountsRepository = Singletons.accountsRepository,
    logger: Logger = LogCatLogger
) : BaseViewModel(accountsRepository, logger), SettingsAdapter.Listener {

    private val _boxSettings = MutableLiveData<Result<List<BoxAndSettings>>>()
    val boxSettings = _boxSettings.share()


    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ALL).collect {
                _boxSettings.value = it
            }
        }
    }

    fun tryAgain() = viewModelScope.safeLaunch {
        boxesRepository.reload(BoxesFilter.ALL)


    }

    override fun enableBox(box: Box) = viewModelScope.safeLaunch {
        boxesRepository.activateBox(box)
    }

    override fun disableBox(box: Box) = viewModelScope.safeLaunch {
        boxesRepository.deactivateBox(box)
    }
}