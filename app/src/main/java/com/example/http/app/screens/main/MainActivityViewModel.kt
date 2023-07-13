package com.example.http.app.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.utils.share
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val accountsSources: AccountsSources
) : ViewModel() {
    private val _username = MutableLiveData<String>()
    val username = _username.share()


    init {
        viewModelScope.launch {
            //listening for the current account and send the username to be displayed in the toolbar
            accountsSources.getAccount().collect(){
                if (it == null){
                    _username.value = ""
                }else{
                    _username.value = "@${it.userName}"
                }
            }
        }
    }
}