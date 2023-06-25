package com.example.nav_components_2_tabs_exercise.model.accounts.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountDbEntity
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountSignInTuple
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountUpdateUsernameTuple
import kotlinx.coroutines.flow.Flow


//          creating a new account.
//          - annotate it with @Dao annotation.
//          - add method for querying ID and Password by email:
//            - annotate the method with @Query
//            - write a SQL-query to fetch ID and Password by Email

//            - return a tuple created before (step #6)
//          - add method for updating username:
//            - use @Update annotation and specify 'entity' parameter = AccountDbEntity::class

//            - add one argument to pass a tuple created before (step #7)
//          - add method for creating a new account
//            - use @Insert annotation
//            - add one argument: AccountDbEntity
//          - add method for querying account info by ID
//            - annotations: @Query
//            - arguments: accountId
//            - return type: Flow<AccountDbEntity>
@Dao
interface AccountsDao {

    @Query("SELECT id, password FROM accounts WHERE email = :email")
    suspend fun findByEmail(email: String): AccountSignInTuple?

    @Update(entity = AccountDbEntity::class)
    suspend fun updateUsername(updateUsernameTuple: AccountUpdateUsernameTuple)

    @Insert
    suspend fun createAccount(accountDbEntity: AccountDbEntity)

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getById(accountId: Long): Flow<AccountDbEntity?>


}