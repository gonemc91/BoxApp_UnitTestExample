package com.example.http.app.model.boxes

import com.example.http.app.Empty
import com.example.http.app.model.*
import com.example.http.app.model.accounts.AccountsRepository
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.boxes.entities.BoxAndSettings
import com.example.http.app.model.boxes.entities.BoxesFilter
import com.example.http.app.utils.async.LazyFlowSubject
import com.example.http.app.Result
import com.example.http.app.Success
import com.example.http.app.Error
import com.example.http.app.model.boxes.entities.Box
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class BoxesRepository(
    private val accountsRepository: AccountsRepository,
    private val boxesSource: BoxesSource
) {
    private var accountResult: Result<Account> = Empty()

    private val boxesLazyFlowSubject = LazyFlowSubject<BoxesFilter, List<BoxAndSettings>>{filter->
        wrapBackendExceptions { boxesSource.getBoxes(filter) }
    }

    /**
     * Get the list of boxes.
     * @return infinity flow, always success; errors are wrapped to [Result]
     */

    fun getBoxesAndSettings(filter: BoxesFilter): Flow<Result<List<BoxAndSettings>>> {
        return accountsRepository.getAccount()
            .onEach {
                accountResult = it
            }
            .flatMapLatest { result ->
                if (result is Success) {
                    // has new account data -> reload boxes
                    boxesLazyFlowSubject.listen(filter)
                } else {
                    flowOf(result.map())
                }
            }
    }

    /**
     * Reload the list boxes.
     * @throws AuthException
     * @throws BackendException
     * @throws ConnectionException
     */

    fun reload(filter: BoxesFilter){
        if (accountResult is Error) {
            //failed to load account -> try it again;
            // after loading account, boxes will be loaded automatically
            accountsRepository.reloadAccount()
        }else{
            boxesLazyFlowSubject.reloadArguments(filter)
        }
    }

    /**
     * Mark the specified box is active. Only active boxes are displayed in dashboard screen.
     * @throws AuthException
     * @throws BackendException
     * @throws ConnectionException
     */

    suspend fun activateBox(box: Box) = wrapBackendExceptions {
        boxesSource.setIsActive(box.id, true)
        boxesLazyFlowSubject.reloadAll(silentMode = true)
    }

    /**
     * Mark the specified box is inactive. Inactive boxes are not displayed in dashboard screen.
     * @throws AuthException
     * @throws BackendException
     * @throws ConnectionException
     */

    suspend fun deactivateBox(box: Box) = wrapBackendExceptions {
        boxesSource.setIsActive(box.id, false)
        boxesLazyFlowSubject.reloadAll(silentMode = true)
    }


}