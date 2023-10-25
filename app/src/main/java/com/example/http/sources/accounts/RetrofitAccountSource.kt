package com.example.http.sources.accounts

import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.accounts.entities.SignUpData
import com.example.http.sources.accounts.entities.SignInRequestEntity
import com.example.http.sources.accounts.entities.SignUpRequestEntity
import com.example.http.sources.accounts.entities.UpdateUsernameRequestEntity
import com.example.http.sources.base.BaseRetrofitSource
import com.example.http.sources.base.RetrofitConfig
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


//todo #7: implement AccountSource methods:
//       -signIn -> should call 'POST/sign-in' and return a token
//       -signUp -> should call 'POST/sign-up'
//       -getAccount -> should call 'GET/me' and return account data
//       -setUsername -> should call 'PUT/me'

@Singleton
class RetrofitAccountSource @Inject constructor(
    config: RetrofitConfig
): BaseRetrofitSource(config), AccountsSources{


    private val accountsApi =
        retrofit.create(AccountsApi:: class.java)
    override suspend fun signIn(
        email: String,
        password: String)
    : String = wrapRetrofitExceptions {
        delay(1000)
        val signInRequestEntity = SignInRequestEntity(
            email=email,
            password=password
        )
        accountsApi.signIn(signInRequestEntity).token
    }

    override suspend fun signUp(
        signUpData: SignUpData) = wrapRetrofitExceptions {
        delay(1000)
        val signUpRequestEntity = SignUpRequestEntity(
            email = signUpData.email,
            username = signUpData.username,
            password = signUpData.password
        )
        accountsApi.signUp(signUpRequestEntity)
    }

    override suspend fun getAccount(): Account = wrapRetrofitExceptions{
        delay(1000)
        accountsApi.getAccount().toAccount()
    }

    override suspend fun setUsername(username: String) = wrapRetrofitExceptions {
        delay(1000)
        val updateUsernameRequestEntity = UpdateUsernameRequestEntity(
            username=username
        )
        accountsApi.setUsername(updateUsernameRequestEntity)

    }
}
