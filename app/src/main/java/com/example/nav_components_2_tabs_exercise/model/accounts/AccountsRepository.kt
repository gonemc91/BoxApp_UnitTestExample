package com.example.nav_components_2_tabs_exercise.model.accounts

import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import kotlinx.coroutines.flow.Flow


/**
 * Repository with account-related actions, e.g. sign-in, sign-up, edit account etc.
 */

interface AccountsRepository {

    /**
     * Whether user signed-in or not .
     */

    suspend fun isSignedIn(): Boolean

    /**
     * Try to sign-in with the email and password.
     * @throws [EmptyFieldException], [AuthExeption]
     */

    suspend fun signIn(email: String, password: String)

    /**
     * Created a new account.
     *  @throws [EmptyFieldException], [PasswordMismatchException], [AccountAlredyExistsExeption]
     */



    suspend fun signUp(signUpData: SignUpData)

    /**
     * Sign-out from the app.
     */

    fun logout()

    /**
     * Get the account info of the current signed-in user.
     */

    fun getAccount(): Flow<Account?>

    /**
     * Change the username of the current signed-in user.
     * @throws [EmptyFieldExeption], [AuthException]
     */

    suspend fun updateAccountUsername(newUserName: String)

}