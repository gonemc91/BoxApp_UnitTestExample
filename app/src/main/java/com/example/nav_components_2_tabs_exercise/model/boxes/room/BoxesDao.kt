package com.example.nav_components_2_tabs_exercise.model.boxes.room

import androidx.room.*
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.BoxAndSettingsTuple
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.BoxDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.views.SettingWithEntitiesTuple
import kotlinx.coroutines.flow.Flow


//           - annotate this interface with @Dao
//           - add method for activating/deactivating box for the specified account;
//             hint: method should have 1 argument of AccountBoxSettingDbEntity type and should
//                   be annotated with @Insert(onConflict=OnConflictStrategy.REPLACE);
//           - add method for fetching boxes and their settings by account ID;
//             hint: there are at least 4 options, the easiest one is to
//                   use Map<BoxDbEntity, AccountBoxSettingDbEntity> as a return type

@Dao
interface BoxesDao {

    @Transaction
    @Query("SELECT * FROM settings_view WHERE account_id = :accountId")
    fun getBoxesAndSettings(accountId: Long): Flow<List<SettingWithEntitiesTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveFlagForBox(accountBoxSettingDbEntity: AccountBoxSettingDbEntity)


}