package com.example.nav_components_2_tabs_exercise.screens.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_components_2_tabs_exercise.model.boxes.BoxesRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.Box
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val boxesRepository: BoxesRepository
): ViewModel(), SettingAdapter.Listener {

    private val _boxSettings = MutableLiveData<List<BoxSetting>>()
    val boxSettings = _boxSettings.share()

    init {
        viewModelScope.launch {
            val allBoxesFlow = boxesRepository.getBoxes(onlyActive = false)
            val activeBoxesFlow = boxesRepository.getBoxes(onlyActive = true)
            val boxSettingsFlow = combine(allBoxesFlow, activeBoxesFlow) { allBoxes, activeBoxes ->
                allBoxes.map {
                    BoxSetting(
                        it,
                        activeBoxes.contains(it)
                    )
                } // O^n2 performance, should be optimized for large lists
            }
            boxSettingsFlow.collect {
                _boxSettings.value = it
            }
        }
    }

    override fun enableBox(box: Box) {
        viewModelScope.launch { boxesRepository.activeBox(box) }
    }

    override fun disableBox(box: Box) {
        viewModelScope.launch { boxesRepository.deactivateBox(box) }
    }


}