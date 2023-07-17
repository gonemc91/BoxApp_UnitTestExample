package com.example.http.app.model.accounts

import com.example.http.app.Result
import com.example.http.app.model.*
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.accounts.entities.SignUpData
import com.example.http.app.model.settings.AppSettings
import com.example.http.app.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow

class AccountsRepository(
    private val accountSource: AccountsSources,
    private val appSettings: AppSettings
) {
    private val accountLazyFlowSubject = LazyFlowSubject<Unit, Account>{
        doGetAccount()
    }

    /**
     * Whether user is signed-in or not.
     */

    fun isSignedIn(): Boolean{
        //user is signed-in if auth token exists
        return appSettings.getCurrentToken() != null
    }

    /**
     * Try to sign-in with the email and password.
     * @throws EmptyFieldException
     * @throws InvalidCreditionalException
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun signIn(email: String, password: String){
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw  EmptyFieldException(Field.Password)

        val token = try{
            accountSource.signIn(email, password)
        }catch (e: Exception){
            if (e is BackendException && e.code == 401){
                //map 401 error for sign-in to InvalidCredentialsException
                throw InvalidCredentialsException(e)
            } else{
                throw e
            }
        }
        // success! got auth token -> save it
        appSettings.setCurrentToken(token)
        // and load account data
        accountLazyFlowSubject.updateAllValues(accountSource.getAccount())
    }

    /**
     * Create a new account.
     * @throws EmptyFieldException
     * @throws PasswordMismatchExceprion
     * @throws AccountAlreadyExistsException
     * @throws ConnectionException
     * @throws BackendException
     */

    suspend fun signUp(signUpData: SignUpData){
        signUpData.validate()
        try {
            accountSource.signUp(signUpData)
        }catch (e: BackendException){
            //user with such email already exists
            if(e.code == 409) throw AccountAlreadyExistsException(e)
            else throw e
        }
    }

    /**
     * Reload account info. Results of reloading are delivered to the flows
     * returned by [getAccount] method.
     */
    fun reloadAccount(){
        accountLazyFlowSubject.reloadAll()
    }

    /**
     * Get the account info of the current signed-in user and listen for all
     * further changes of the account data.
     * If user is not logged-in an empty result is emitted.
     * @return infinite flow, always succes; errors are wrapped to [Result]
     */
    fun getAccount(): Flow<Result<Account>> {
        return accountLazyFlowSubject.listen(Unit)
    }

    /**
     * Change the username of the current signed-in user. An update account
     * entities is delivered to the flow returned by [getAccount] call after
     * calling this method.
     * @throws EmptyFieldException
     * @throws EmptyFieldException
     * @throws AuthException
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */

    suspend fun updateAccountUsername(newUsername: String) = wrapBackendExceptions {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)
        accountSource.setUsername(newUsername)
        accountLazyFlowSubject.updateAllValues(accountSource.getAccount())
    }

    /**
     * Clear JWT-token saved in settings.
     */

    fun logout() {
        appSettings.setCurrentToken(null)
        accountLazyFlowSubject.updateAllValues(null)
    }

    private suspend fun doGetAccount(): Account = wrapBackendExceptions {
        try{
            accountSource.getAccount()
        }catch (e: BackendException){
            // account has been delted = session expired = AuthException
            if (e.code == 404) throw AuthException(e)
            else throw e
        }
    }


}