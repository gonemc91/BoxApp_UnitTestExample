package com.example.http.app.model.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AppSettings] based on [SharedPreferences].
 *
 */
@Singleton//singleton scope hilt var 2
class SharedPreferencesAppSettings @Inject constructor (//"annotation" dependencies come through the constructor
    @ApplicationContext appContext: Context // inform that need use appContext (qualifier)
): AppSettings {

    private val sharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)



    override fun setCurrentToken(token: String?) {
        val editor = sharedPreferences.edit()
        if(token == null)
            editor.remove(PREF_CURRENT_ACCOUNT_TOKEN)
        else
            editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
        editor.apply()
    }
    override fun getCurrentToken(): String? = sharedPreferences.getString(PREF_CURRENT_ACCOUNT_TOKEN, null)

    companion object{
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
    }



}