package com.example.nav_components_2_tabs_exercise

import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.model.accounts.InMemoryAccountsRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.BoxesRepository
import com.example.nav_components_2_tabs_exercise.model.boxes.InMemoryBoxesRepository

object Repositories {

    val accountsRepository: AccountsRepository = InMemoryAccountsRepository()

    val boxesRepository: BoxesRepository = InMemoryBoxesRepository(accountsRepository)

}