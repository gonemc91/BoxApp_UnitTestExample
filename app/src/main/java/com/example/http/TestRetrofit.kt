package com.example.http

import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class SignInRequestBodyTest( //data class for Request
    val email: String,
    val password: String
)

data class SignInResponseBodyTest( //data class for Response
    val token:String
)

interface Api{
    //Retrofit Function
    @POST("sign-in") //annotation POST
    suspend fun signIn(@Body body: SignInRequestBodyTest): SignInResponseBodyTest //annotation Body suspend request
}


fun main() = runBlocking{
    //1) create interceptor
    val loggingInterceptor = HttpLoggingInterceptor() // logger for request
        .setLevel(HttpLoggingInterceptor.Level.BODY) //level logging MAX
//2)create client
    val client = OkHttpClient.Builder() //create client
        .addInterceptor(loggingInterceptor) //add interceptor
        .build()

    //3) create moshi for parsing data
val moshi = Moshi.Builder().build()

    //4)create adapter for parsing json request and json format response
    val moshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    //5) create retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:12345")
        .client(client)
        .addConverterFactory(moshiConverterFactory)
        .build()

    //using
    val api = retrofit.create(Api::class.java)  //realization interface Api with instruction for task (sign-in)

    val requestBody = SignInRequestBodyTest( //init request
        email = "admin@google.com",
        password = "123"
    )

    val response  = api.signIn(requestBody)


    println("TOKEN: ${response.token}")
}