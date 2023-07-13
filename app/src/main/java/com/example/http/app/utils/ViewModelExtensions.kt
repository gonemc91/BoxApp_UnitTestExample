package com.example.http.app.utils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.requireValue(): T{
    return this.value ?: throw java.lang.IllegalStateException("Value is empty")
}