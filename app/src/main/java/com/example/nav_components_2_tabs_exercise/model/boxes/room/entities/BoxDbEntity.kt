package com.example.nav_components_2_tabs_exercise.model.boxes.room.entities

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.Box


//          - fields: id, colorName, colorValue.
//          - add toBox() method for mapping BoxDbEntity instances to Box instances.
//          - hint: use the same annotations as for AccountDbEntity (@Entity, @PrimaryKey, @ColumnInfo).
//          - DO NOT FORGET to add this entity to the @Database annotation of AppDatabase class

@Entity(
    tableName = "boxes"
)
data class BoxDbEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "color_name") val colorName: String,
    @ColumnInfo(name = "color_value") val colorValue: String
) {
    fun toBox(): Box = Box(
        id = id,
        colorName = colorName,
        colorValue = Color.parseColor(colorValue)
    )
}