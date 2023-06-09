package com.example.nav_components_2_tabs_exercise.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {
    private val _username = MutableLiveData<String>()
    val username = _username.share()


    init {
        viewModelScope.launch {
            //listening for the current account and send the username to be displayed in the toolbar
            accountsRepository.getAccount().collect(){
                if (it == null){
                    _username.value = ""
                }else{
                    _username.value = "@${it.userName}"
                }
            }
        }
    }
}