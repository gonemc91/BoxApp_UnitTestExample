package com.example.nav_components_2_tabs_exercise.model.accounts

import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import kotlinx.coroutines.flow.Flow
import com.example.nav_components_2_tabs_exercise.model.AppException.*


/**
 * Repository with account-related actions, e.g. sign-in, sign-up, edit account etc.
 */
 interface AccountsRepository {


    /**
     * Whether user is signed-in or not.
     */

    suspend fun isSignedIn(): Boolean

    /**
     * Try to sign-in with the email and password
     * @throws [EmpyFieldException]
     * @throws [AuthException]
     * @throws [StorageException]
     */

    suspend fun signIn(email: String, password: String)

    /**
     * Create a new account.
     * @throws [EmptyFieldException]
     * @throws [PasswordMismatchException]
     * @throws [AccountAlreadyExistsException]
     * @throws [StorageException]
     */
    suspend fun signUp(signUpData: SignUpData)

    /**
     * Sign-out from the app.
     */
    suspend fun logout()
    /**
     * Get the account info of the current signed-in user.
     */

    suspend fun getAccount(): Flow<Account?>

    /**
     * change the username of the current signed-in user.
     * @throws [EmptyFieldException]
     * @throws [AuthException]
     * @throws [StorageException]
     */

    suspend fun updateAccountUsername(newUserName: String)


}
