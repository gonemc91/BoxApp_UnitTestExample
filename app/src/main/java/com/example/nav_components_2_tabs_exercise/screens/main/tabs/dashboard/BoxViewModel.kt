package com.example.nav_components_2_tabs_exercise.screens.main.tabs.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_components_2_tabs_exercise.model.boxes.BoxesRepository
import com.example.nav_components_2_tabs_exercise.utils.MutableLiveEvent
import com.example.nav_components_2_tabs_exercise.utils.publishEvent
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoxViewModel(
    private val boxId: Long,
    private val boxesRepository: BoxesRepository
) : ViewModel() {
    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()


    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(onlyActive = true)
                .map { boxes -> boxes.firstOrNull{it.box.id == boxId} }
                .collect{currentBox ->
                    _shouldExitEvent.publishEvent(currentBox == null)
                }
        }
    }

}