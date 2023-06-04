package com.example.nav_components_2_tabs_exercise.model.boxes.entities

import androidx.annotation.StringRes

data class Box (
    val id: Int,
    @StringRes val colorNameRes: Int,
    val colorValue: Int
        )