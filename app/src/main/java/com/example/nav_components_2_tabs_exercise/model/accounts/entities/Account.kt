package com.example.nav_components_2_tabs_exercise.model.accounts.entities

/**
 * Information about the user.
 */

data class Account (
    val userName: String,
    val email: String,
    val createdAt: Long = UNKNOWN_CREATED_AT
){
    companion object{
        const val UNKNOWN_CREATED_AT = 0L
    }
}