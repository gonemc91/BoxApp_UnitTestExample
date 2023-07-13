package com.example.http.app.screens.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.app.model.boxes.entities.Box
import com.example.http.app.utils.share
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val boxesSource: BoxesSource
) : ViewModel() {

    private val _boxes = MutableLiveData<List<Box>>()
    val boxes = _boxes.share()


    init {
        viewModelScope.launch {
            boxesSource.getBoxesAndSettings(onlyActive = true).collect { list->
                _boxes.value = list.map{ it.box}
            }
        }
    }

}