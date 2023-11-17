package com.example.http.domain.boxes

import com.example.http.domain.AuthException
import com.example.http.domain.BackendException
import com.example.http.domain.ConnectionException
import com.example.http.domain.Empty
import com.example.http.domain.Error
import com.example.http.domain.Result
import com.example.http.domain.Success
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.domain.accounts.entities.Account
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.domain.wrapBackendExceptions
import com.example.http.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoxesRepository @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val boxesSource: BoxesSource
) {
    private var accountResult: Result<Account> = Empty()

    private val boxesLazyFlowSubject = LazyFlowSubject<BoxesFilter, List<BoxAndSettings>>{ filter->
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