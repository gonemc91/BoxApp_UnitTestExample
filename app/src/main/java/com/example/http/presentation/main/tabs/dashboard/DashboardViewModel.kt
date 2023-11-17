package com.example.http.presentation.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.http.utils.logger.Logger
import com.example.http.domain.Result
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.boxes.BoxesRepository
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.presentation.base.BaseViewModel
import com.example.http.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _boxes = MutableLiveData<Result<List<Box>>>()
    val boxes = _boxes.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).collect { result ->
                _boxes.value = result.map { list -> list.map { it.box } }
            }
        }
    }

    fun reload() = viewModelScope.launch {
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)
    }

}