package com.example.http.app.model.settings

import android.content.Context
/**
 * Implementation of [AppSettings] based on [SharedPreferences].
 */
class SharedPreferencesAppSettings(
    appContext: Context
): AppSettings {

    private val sharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun getCurrentToken(): String? = sharedPreferences.getString(PREF_CURRENT_ACCOUNT_TOKEN, null)

    override fun setCurrentToken(token: String?) {
        val editor = sharedPreferences.edit()
        if(token == null)
            editor.remove(PREF_CURRENT_ACCOUNT_TOKEN)
        else
            editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
        editor.apply()
    }

    companion object{
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
    }



}