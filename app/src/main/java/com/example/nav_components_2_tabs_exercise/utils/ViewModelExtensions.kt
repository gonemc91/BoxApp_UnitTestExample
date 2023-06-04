package com.example.nav_components_2_tabs_exercise.utils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.requireValue(): T{
    return this.value ?: throw java.lang.IllegalStateException("Value is empty")
}