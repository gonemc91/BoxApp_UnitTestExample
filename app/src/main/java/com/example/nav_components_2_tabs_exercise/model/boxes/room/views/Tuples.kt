package com.example.nav_components_2_tabs_exercise.model.boxes.room.views

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.BoxDbEntity

/**
 * Tuple foe querying rows from 'settings_view' with related entities:
 * [AccountEntity] by value in the 'account_id' column and [BoxDbEntity] by
 * value in the 'box_id' column.
 */

data class SettingWithEntitiesTuple(
    @Embedded val settingDbEntity: SettingsDbView,


    @Relation(
        parentColumn =  "account_id",
        entityColumn = "id"
    )

    val accountDbEntity: AccountDbEntity,

    @Relation(
        parentColumn = "box_id",
        entityColumn =  "id"
    )
    val boxDbEntity: BoxDbEntity
)