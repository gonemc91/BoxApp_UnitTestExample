package com.example.nav_components_2_tabs_exercise.model.accounts.room.entites

import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.BoxAndSettings

/**
 * Account info with all boxes and their settings
 */


data class AccountFullData(
    val account: Account,
    val boxesAndSettings: List<BoxAndSettings>

)
