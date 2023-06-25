package com.example.nav_components_2_tabs_exercise.model.boxes.room.entities

import androidx.room.*
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountDbEntity


//          - fields: accountId, boxId, isActive.
//          - use composite primary key: (accountId + boxId);
//            hint: use 'primaryKeys' parameter of @Entity annotation.
//          - add index for the second column of composite primary key (boxId);
//            hint: use 'indices' parameter of @Entity annotation.
//          - add foreign keys for accountId and boxId fields. Foreign keys should point to the
//            AccountDbEntity and BoxDbEntity with CASCADE option for update/delete actions.
//            hint: use 'foreignKeys' parameter of @Entity annotation.
//          - DO NOT FORGET to add this entity to the @Database annotation of AppDatabase class

@Entity(
    tableName = "accounts_boxes_settings",
    foreignKeys = [
        ForeignKey(
            entity = AccountDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BoxDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["box_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["account_id", "box_id"],
    indices = [
        Index("box_id")
    ]
)

data class AccountBoxSettingDbEntity(
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "box_id") val boxId: Long,
    @Embedded val settings: SettingsTuple
)