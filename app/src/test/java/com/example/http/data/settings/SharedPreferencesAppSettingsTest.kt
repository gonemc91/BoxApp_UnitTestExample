package com.example.http.data.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.example.http.testutils.arranged
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verifySequence
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SharedPreferencesAppSettingsTest {

    @get: Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var preferences: SharedPreferences

    @RelaxedMockK
    lateinit var editor: Editor

    private lateinit var settings: SharedPreferencesAppSettings

    private val expectedTokenKey = "currentToken"

    @Before
    fun setUp(){
        every { context.getSharedPreferences(any(), any()) } returns preferences
        every { preferences.edit() } returns editor

        settings = SharedPreferencesAppSettings(context)
    }

    @Test
    fun setCurrentTokenPutsValueToPreferences(){
        arranged()

        settings.setCurrentToken("token")

        verifySequence {
            preferences.edit()
            editor.putString(expectedTokenKey, "token")
            editor.apply()
        }
    }

    @Test
    fun setCurrentTokenWithNullRemovesValueFromPreference(){
        arranged()

        settings.setCurrentToken(null)

        verifySequence {
            preferences.edit()
            editor.remove(expectedTokenKey)
            editor.apply()
        }
    }

    @Test
    fun getCurrentTokenReturnValueFromPreference(){
        every { preferences.getString(expectedTokenKey, null) } returns "token"

        val token = settings.getCurrentToken()

        Assert.assertEquals("token", token)
    }




}