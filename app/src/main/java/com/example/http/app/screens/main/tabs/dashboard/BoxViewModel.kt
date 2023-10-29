package com.example.http.app.screens.main.tabs.dashboard

import androidx.lifecycle.viewModelScope
import com.example.http.app.Success
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.model.boxes.BoxesRepository
import com.example.http.app.model.boxes.entities.BoxesFilter
import com.example.http.app.screens.base.BaseViewModel
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.logger.Logger
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class BoxViewModel @AssistedInject constructor(
    @Assisted boxId: Long,
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()


    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
                .map { res -> res.map { boxes -> boxes.firstOrNull { it.box.id == boxId } } }
                .collect { res ->
                    _shouldExitEvent.publishEvent(res is Success && res.value == null)
                }
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(boxId: Long) : BoxViewModel
    }

}