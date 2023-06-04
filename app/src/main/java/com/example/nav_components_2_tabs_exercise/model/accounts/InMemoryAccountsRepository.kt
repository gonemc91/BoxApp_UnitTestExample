package com.example.nav_components_2_tabs_exercise.model.accounts

import com.example.nav_components_2_tabs_exercise.model.AccountAlreadyExistsException
import com.example.nav_components_2_tabs_exercise.model.AuthException
import com.example.nav_components_2_tabs_exercise.model.EmptyFieldException
import com.example.nav_components_2_tabs_exercise.model.Field
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Simple implementation of [AccountsRepository] which holds accounts data in the app memory.
 */

class InMemoryAccountsRepository: AccountsRepository {

    private var currentAccountFlow = MutableStateFlow<Account?>(null)

    private val accounts = mutableListOf(
       AccountRecord(
           account = Account(
               userName = "admin",
               email = "admin@google.com"
           ),
           password = "123"
       )
    )

    init {
        currentAccountFlow.value = accounts[0].account
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2000)
        return currentAccountFlow.value != null
}

    override suspend fun signIn(email: String, password: String) {
        if(email.isBlank()) throw EmptyFieldException(Field.Email)
        if(password.isBlank()) throw EmptyFieldException(Field.Password)

        delay(1000)
        val record = getRecordByEmail(email)
        if(record != null && record.password == password){
            currentAccountFlow.value = record.account
        }else{
            throw AuthException()
        }
    }

    override suspend fun signUp(signUpData: SignUpData) {
        signUpData.validate()

        delay(1000)
        val accountRecord = getRecordByEmail(signUpData.email)
        if (accountRecord != null) throw AccountAlreadyExistsException()

        val newAccount = Account(
            userName = signUpData.username,
            email = signUpData.email,
            createdAt = System.currentTimeMillis()
        )
        accounts.add(AccountRecord(newAccount, signUpData.password))
    }

    override fun logout() {
        currentAccountFlow.value = null
    }

    override fun getAccount(): Flow<Account?> = currentAccountFlow

    override suspend fun updateAccountUsername(newUserName: String) {
        if(newUserName.isBlank()) throw EmptyFieldException(Field.Username)

        delay(1000)
        val currentAccount = currentAccountFlow.value ?: throw AuthException()

        val updateAccount = currentAccount.copy(userName = newUserName)
        currentAccountFlow.value = updateAccount
        val currentRecord = getRecordByEmail(currentAccount.email) ?: throw AuthException()
        currentRecord.account = updateAccount

    }

    private fun getRecordByEmail(email: String) = accounts.firstOrNull{ it.account.email == email }

    private class AccountRecord(
        var account: Account,
        val password: String
    )


}