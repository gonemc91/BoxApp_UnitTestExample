package com.example.http.presentation.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.utils.logger.Logger
import com.example.http.domain.Result
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.base.BaseViewModel
import com.example.http.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
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