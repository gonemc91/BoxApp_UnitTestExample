package com.example.http.data.accounts

import com.example.http.data.accounts.entities.GetAccountResponseEntity
import com.example.http.data.accounts.entities.SignInRequestEntity
import com.example.http.data.accounts.entities.SignInResponseEntity
import com.example.http.data.accounts.entities.SignUpRequestEntity
import com.example.http.data.accounts.entities.UpdateUsernameRequestEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


//todo #5 add 4 methods for making requests related to accounts:
//     -'POST/sign-in'
//     -'POST/sign-up'
//     -'GET/me'
//     -'PUT/me'
//     Hint: use entities located in '*.sources.accounts.entities' package

interface AccountsApi{

    @POST("sign-in")
    suspend fun  signIn(@Body signInRequestEntity: SignInRequestEntity): SignInResponseEntity

    @POST("sign-up")
    suspend fun signUp(@Body signUpRequestEntity: SignUpRequestEntity)

    @GET("me")
    suspend fun getAccount(): GetAccountResponseEntity

    @PUT("me")
    suspend fun setUsername(@Body body: UpdateUsernameRequestEntity)


}