package com.example.http.testutils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.http.domain.accounts.AccountsRepository
import com.example.http.utils.logger.Logger
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule

open class ViewModelTest {


    /**
     * Substitution "MainThread" for uses coroutines in UnitTests ViewModel
     */
    @get:Rule
    val testViewModelScopeRule = TestViewModelScopeRule()
    /**
     * Substitution "MainThread" for uses  livedata in UnitTests ViewModel
     */
    @get:Rule
    val instantTaskExecutorsRule = InstantTaskExecutorRule()

    @get: Rule
    val mockRule = MockKRule(this)

    @RelaxedMockK
    lateinit var logger: Logger

    @RelaxedMockK
    lateinit var accountsRepository: AccountsRepository


}
