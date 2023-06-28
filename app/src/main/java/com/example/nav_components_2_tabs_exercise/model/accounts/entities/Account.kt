package com.example.nav_components_2_tabs_exercise.model.accounts.entities

/**
 * Information about the user.
 */

data class Account (
    val id: Long,
    val userName: String,
    val email: String,
    val createdAt: Long = UNKNOWN_CREATED_AT
){

    /**
     * Let's assume that there is only one admin and its ID = 1 XD
     */

    fun isAdmin() = id == ADMIN_ACCOUNT_ID


    companion object{
        const val UNKNOWN_CREATED_AT = 0L

        private const val  ADMIN_ACCOUNT_ID = 1L
    }
}