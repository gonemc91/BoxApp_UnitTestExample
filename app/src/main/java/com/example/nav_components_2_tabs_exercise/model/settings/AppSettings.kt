package com.example.nav_components_2_tabs_exercise.model.settings

interface AppSettings {

    /**
     * Get account ID of the current logged-in user or [NO_ACCOUNT_ID] otherwise
     */

    fun getCurrentAccountId(): Long

    /**
     *Set account ID of the logged-in user or [NO_ACCOUNT_ID] if user is not logged-in
     */


    fun setCurrentAccountId(accountId: Long)

    /**
     * Indicates that there is not logged-in user.
     */

    companion object{
        const val NO_ACCOUNT_ID = -1L
    }


}