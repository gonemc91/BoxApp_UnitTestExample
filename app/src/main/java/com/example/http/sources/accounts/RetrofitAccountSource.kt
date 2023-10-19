package com.example.http.sources.accounts

import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.accounts.entities.SignUpData
import com.example.http.sources.base.BaseRetrofitSource
import com.example.http.sources.base.RetrofitConfig
import kotlinx.coroutines.delay


//todo #7: implement AccountSource methods:
//       -signIn -> should call 'POST/sign-in' and return a token
//       -signUp -> should call 'POST/sign-up'
//       -getAccount -> should call 'GET/me' and return account data
//       -setUsername -> should call 'PUT/me'


class RetrofitAccountSource (
    config: RetrofitConfig
): BaseRetrofitSource(config), AccountsSources{
    override suspend fun signIn(
        email: String,
        password: String)
    : String {
        delay(1000)
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        signUpData: SignUpData) {
        delay(1000)
        TODO("Not yet implemented")
    }

    override suspend fun getAccount(): Account {
        delay(1000)
        TODO("Not yet implemented")
    }

    override suspend fun setUsername(username: String) {
        delay(1000)
        TODO("Not yet implemented")
    }
}
