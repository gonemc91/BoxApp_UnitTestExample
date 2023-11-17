package com.example.http.utils.resources

import androidx.annotation.StringRes

interface Resources {
    fun getString(@StringRes stringRes: Int):String
}