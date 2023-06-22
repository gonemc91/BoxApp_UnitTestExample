package com.example.nav_components_2_tabs_exercise.model.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppSQLiteHelper (private val applicationContext: Context): SQLiteOpenHelper(applicationContext,"database.db", null, 1) {



    override fun onCreate(dB: SQLiteDatabase) {
        val sqL = applicationContext.assets.open("db_init.sql").bufferedReader().use {
            it.readText()
        }
        sqL.split(';')
            .filter { it.isNotBlank() }
            .forEach{
               dB.execSQL(it)
            }
    }

    override fun onUpgrade(dB: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }



}

