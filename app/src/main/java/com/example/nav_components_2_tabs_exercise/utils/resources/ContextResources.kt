package com.example.nav_components_2_tabs_exercise.utils.resources

import android.content.Context

class ContextResources(
    private val context: Context
) : Resources {
    override fun getString(stringRes: Int): String {
       return context.getString(stringRes)
    }
}