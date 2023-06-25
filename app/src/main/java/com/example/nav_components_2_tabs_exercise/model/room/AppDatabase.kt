package com.example.nav_components_2_tabs_exercise.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nav_components_2_tabs_exercise.model.accounts.room.AccountsDao
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.BoxesDao
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.BoxDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.views.SettingsDbView

@Database(
    version = 1,
    entities = [
        AccountDbEntity::class,
        BoxDbEntity::class,
        AccountBoxSettingDbEntity::class,
    ],
    views = [
        SettingsDbView::class
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAccountDao(): AccountsDao

    abstract fun getBoxesDao(): BoxesDao

}