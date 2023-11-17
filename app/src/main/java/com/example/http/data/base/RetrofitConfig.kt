package com.example.http.data.base

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton


/**
 * All stuffs required for making HTTP-request with Retrofit client and
 * for parsing JSON-message.
 */

@Singleton
class RetrofitConfig @Inject constructor(
    val retrofit: Retrofit,
    val moshi: Moshi
)
