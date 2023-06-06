package com.example.nav_components_2_tabs_exercise.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository

class MainActivityViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {
    private val username = MutableLiveData<String>()

}