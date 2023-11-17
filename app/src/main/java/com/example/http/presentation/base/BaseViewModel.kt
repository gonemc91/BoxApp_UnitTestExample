package com.example.http.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.domain.AuthException
import com.example.http.domain.BackendException
import com.example.http.domain.ConnectionException
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.utils.MutableLiveEvent
import com.example.http.utils.MutableUnitLiveEvent
import com.example.http.utils.logger.Logger
import com.example.http.utils.publishEvent
import com.example.http.utils.share
import com.example.nav_components_2_tabs_exercise.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


open class BaseViewModel(
    val accountsRepository: AccountsRepository,
    val logger: Logger
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    private val _showAuthErrorAndRestartEvent = MutableUnitLiveEvent()
    val showAuthErrorAndRestartEvent = _showAuthErrorAndRestartEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch {
            try {
                block.invoke(this)
            }catch (e: ConnectionException){
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.connection_error)
            }catch (e: BackendException){
                logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            }catch (e: AuthException){
                logError(e)
                _showAuthErrorAndRestartEvent.publishEvent()
            }catch (e: Exception){
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.internal_error)
            }


        }
    }

    fun logError(e: Throwable){
        logger.error(javaClass.simpleName, e)
    }

    fun logout(){
        accountsRepository.logout()
    }




}