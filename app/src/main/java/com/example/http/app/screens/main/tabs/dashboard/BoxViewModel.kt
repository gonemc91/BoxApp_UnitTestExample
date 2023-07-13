package com.example.http.app.screens.main.tabs.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.app.utils.MutableLiveEvent
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.share
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoxViewModel(
    private val boxId: Long,
    private val boxesSource: BoxesSource
) : ViewModel() {
    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()


    init {
        viewModelScope.launch {
            boxesSource.getBoxesAndSettings(onlyActive = true)
                .map { boxes -> boxes.firstOrNull{it.box.id == boxId} }
                .collect{currentBox ->
                    _shouldExitEvent.publishEvent(currentBox == null)
                }
        }
    }

}