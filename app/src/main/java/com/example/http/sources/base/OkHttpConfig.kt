package com.example.http.sources.base

import com.google.gson.Gson
import okhttp3.OkHttpClient


/**
 * Base URL, OkHttp client and GSON parser in the OkHttpConfig class
 */
class OkHttpConfig(
    val baseUrl: String, //data base URL http://127.0.0.1:12345/
    val client: OkHttpClient, //client from OkHttp library for request from application
    val gson: Gson // parses google Json
)