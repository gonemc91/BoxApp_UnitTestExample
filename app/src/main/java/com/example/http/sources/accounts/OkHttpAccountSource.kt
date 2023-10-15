package com.example.http.sources.accounts

import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.accounts.entities.SignUpData
import com.example.http.sources.accounts.entities.GetAccountResponseEntity
import com.example.http.sources.accounts.entities.SignInRequestEntity
import com.example.http.sources.accounts.entities.SignInResponseEntity
import com.example.http.sources.accounts.entities.SignUpRequestEntity
import com.example.http.sources.accounts.entities.UpdateUsernameRequestEntity
import com.example.http.sources.base.BaseOkHttpSource
import com.example.http.sources.base.OkHttpConfig
import kotlinx.coroutines.delay
import okhttp3.Request


//          - signIn() -> for exchanging email+password to token
//          - signUp() -> for creating a new account
//          - getAccount() -> for fetching account info
//          - setUsername() -> for editing username

class OkHttpAccountSource(
    config: OkHttpConfig
): BaseOkHttpSource(config), AccountsSources {

    override suspend fun signIn(email: String, password: String): String {
        delay(1000)
        val signInRequestEntity = SignInRequestEntity(
            email = email,
            password = password
        )
        val request = Request.Builder() // для добавления заголовков использовать header
            .post(signInRequestEntity.toJsonRequestBody())
            .endpoint("/sign-in")
            .build()
        val response =  client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<SignInResponseEntity>().token
    }

    override suspend fun signUp(signUpData: SignUpData) {
        delay(1000)
        val signUpRequestEntity = SignUpRequestEntity(
            email = signUpData.email,
            username = signUpData.username,
            password = signUpData.password
        )
        val request = Request.Builder()
            .post(signUpRequestEntity.toJsonRequestBody())
            .endpoint("/sign-up")
            .build()
        client.newCall(request).suspendEnqueue()
    }

    override suspend fun getAccount(): Account {
        delay(1000)
        val request = Request.Builder()
            .get()
            .endpoint("/me")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        val accountEntity = response.parseJsonResponse<GetAccountResponseEntity>()
        return  accountEntity.toAccount()
    }

    override suspend fun setUsername(username: String) {
        delay(1000)
        val updateUsernameRequestEntity = UpdateUsernameRequestEntity(
            username = username
        )
        val request = Request.Builder()
            .put(updateUsernameRequestEntity.toJsonRequestBody())
            .endpoint("/me")
            .build()
        client.newCall(request).suspendEnqueue()
    }
}