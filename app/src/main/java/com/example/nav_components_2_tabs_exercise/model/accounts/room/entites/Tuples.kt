package com.example.nav_components_2_tabs_exercise.model.accounts.room.entites

import androidx.room.*
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.entities.BoxDbEntity
import com.example.nav_components_2_tabs_exercise.model.boxes.room.views.SettingsDbView

//          Tuple classes should not be annotated with @Entity but their fields may be
//          annotated with @ColumnInfo.
data class AccountSignInTuple(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "password") val password: String
)

//          Such tuples should contain a primary key ('id') in order to notify Room which row you want to update
data class AccountUpdateUsernameTuple(
    @ColumnInfo(name = "id")@PrimaryKey val id: Long,
    @ColumnInfo(name =  "username")val username: String
)

data class AccountAndEditBoxesTuple(
    @Embedded val accountDbEntity: AccountDbEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AccountBoxSettingDbEntity::class,
            parentColumn = "account_id",
            entityColumn = "box_id"
        )
    )
    val boxes: List<BoxDbEntity>,
)

data class AccountAndAllSettingsTuple(
    @Embedded val accountDbEntity: AccountDbEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "account_id",
        entity = SettingsDbView::class

    )
    val settings: List<SettingsAndBoxTuple>

)

data class SettingsAndBoxTuple(
    @Embedded val accountBoxSettingsDbEntity: SettingsDbView,
    @Relation(
        parentColumn = "box_id",
        entityColumn = "id"
    )
    val boxDbEntity: BoxDbEntity


)