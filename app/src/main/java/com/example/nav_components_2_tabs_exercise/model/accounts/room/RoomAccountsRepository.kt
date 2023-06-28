package com.example.nav_components_2_tabs_exercise.model.accounts.room

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.example.nav_components_2_tabs_exercise.model.AccountAlreadyExistsException
import com.example.nav_components_2_tabs_exercise.model.AuthException
import com.example.nav_components_2_tabs_exercise.model.EmptyFieldException
import com.example.nav_components_2_tabs_exercise.model.Field
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountDbEntity
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountFullData
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.AccountUpdateUsernameTuple
import com.example.nav_components_2_tabs_exercise.model.accounts.room.entites.SettingsAndBoxTuple
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.BoxAndSettings
import com.example.nav_components_2_tabs_exercise.model.room.wrapSQLiteException
import com.example.nav_components_2_tabs_exercise.model.settings.AppSettings
import com.example.nav_components_2_tabs_exercise.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class RoomAccountsRepository(
    private val accountsDao: AccountsDao,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
): AccountsRepository{

    private val currentAccountFlow = AsyncLoader{
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }


    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2000)
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: String) = wrapSQLiteException(ioDispatcher) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        delay(1000)

        val accountId = findAccountIdByEmailAndPassword(email, password)
        appSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)

        return@wrapSQLiteException
    }

    override suspend fun signUp(signUpData: SignUpData) = wrapSQLiteException(ioDispatcher) {
        signUpData.validate()
        delay(1000)
        createAccount(signUpData)
    }

    override suspend fun logout() {
        appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .flatMapLatest { accountId ->
                if (accountId.value == AppSettings.NO_ACCOUNT_ID) {
                    flowOf(null)
                } else {
                    getAccountById(accountId.value)
                }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) = wrapSQLiteException(ioDispatcher) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)
        delay(1000)
        val accountId = appSettings.getCurrentAccountId()
        if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

        updateUsernameForAccountId(accountId, newUsername)

        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException
    }

    override suspend fun getAllData(): Flow<List<AccountFullData>> {
        val account = getAccount().first()
        if(account == null || !account.isAdmin()) throw AuthException()
        return accountsDao.getAllData()
            .map { accountsAndSettings->
                accountsAndSettings.map {accountAndSettingsTuple ->  
                    AccountFullData(
                        account = accountAndSettingsTuple.accountDbEntity.toAccount(),
                        boxesAndSettings = accountAndSettingsTuple.settings.map {SettingsAndBoxTuple->
                            BoxAndSettings(
                                box = SettingsAndBoxTuple.boxDbEntity.toBox(),
                                isActive = SettingsAndBoxTuple.accountBoxSettingsDbEntity.settings.isActive
                            )

                        }
                    )
                }
            }
    }


    private suspend fun findAccountIdByEmailAndPassword(email: String, password: String): Long {
        val tuple = accountsDao.findByEmail(email) ?: throw AuthException()
        if(tuple.password != password) throw  AuthException()
        return tuple.id
        //("#11: use AccountsDao to fetch ID and Password by Email. " +
        //"Throw AuthException if there is no account with such email or password is invalid."
    }

    //           create a new AccountDbEntity from SignUpData here and insert it to the database by
    //           using AccountsDao.
    //           Catch SQLiteConstraintException and rethrow AccountAlreadyExistsException.
    //           SQLiteConstraintException is thrown by DAO in case if there is another
    //           account with the same email address

    private suspend fun createAccount(signUpData: SignUpData) {
        try {
            val entity = AccountDbEntity.fromSignUpData(signUpData)
            accountsDao.createAccount(entity)
        }catch (e: SQLiteConstraintException){
            val appException = AccountAlreadyExistsException()
            appException.initCause(appException)
            throw appException
        }

    }
    // get account info by ID; do not forget to map AccountDbEntity to Account here")
    private fun getAccountById(accountId: Long): Flow<Account?> {
        return accountsDao.getById(accountId).map {accountDbEntity -> accountDbEntity?.toAccount()}
    }

    //           update username for the account with specified ID.
    //           hint: use a tuple class created before (step #7)


    private suspend fun updateUsernameForAccountId(accountId: Long, newUsername: String) {
        accountsDao.updateUsername(
            AccountUpdateUsernameTuple(accountId, newUsername)
        )
    }



    private class AccountId(val value: Long)
}