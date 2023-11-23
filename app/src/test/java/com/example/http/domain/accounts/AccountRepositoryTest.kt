package com.example.http.domain.accounts

import com.example.http.domain.AccountAlreadyExistsException
import com.example.http.domain.AuthException
import com.example.http.domain.BackendException
import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Field
import com.example.http.domain.InvalidCredentialsException
import com.example.http.domain.Result
import com.example.http.domain.accounts.entities.Account
import com.example.http.domain.accounts.entities.SignUpData
import com.example.http.domain.settings.AppSettings
import com.example.http.domain.wrapBackendExceptions
import com.example.http.testutils.arranged
import com.example.http.testutils.catch
import com.example.http.testutils.createAccount
import com.example.http.testutils.wellDone
import com.example.http.utils.async.LazyFlowFactory
import com.example.http.utils.async.LazyFlowSubject
import com.example.http.utils.async.SuspendValueLoader
import io.mockk.CapturingSlot
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountRepositoryTest {

    @get:Rule
    val rule = MockKRule(this)

    @RelaxedMockK
    lateinit var accountsSource: AccountsSource

    @RelaxedMockK
    lateinit var appSettings: AppSettings

    @RelaxedMockK
    lateinit var lazyFlowFactory: LazyFlowFactory

    @RelaxedMockK
    lateinit var lazyFlowSubject: LazyFlowSubject<Unit, Account>

    private lateinit var accountsRepository: AccountsRepository

    @Before
    fun setUp(){
        every {
            lazyFlowFactory.createLazyFlowSubject<Unit, Account>(any())
        }returns lazyFlowSubject

        accountsRepository = createRepository()

        mockkStatic("com.example.http.domain.ExceptionsKt")
    }



    @After
    fun tearDown(){
       unmockkStatic("com.example.http.domain.ExceptionsKt")
    }




    @Test
    fun isSignedInWithSavedTokenReturnsTrue(){
        every { appSettings.getCurrentToken()  } returns "some-token"

        val isSignedIn = accountsRepository.isSignedIn()

        assertTrue(isSignedIn)
    }

    @Test
    fun isSignedInWithoutSavedTokenReturnsFalse() {
        every { appSettings.getCurrentToken() } returns null

        val isSigneIn = accountsRepository.isSignedIn()

        assertFalse(isSigneIn)
    }


    @Test
    fun signInWithEmptyPasswordThrowsException() = runTest {
        arranged()

        val exception: EmptyFieldException = catch {
            accountsRepository.signIn("email", "   ")
        }


        assertEquals(Field.Password, exception.field)
        coVerify {
            accountsSource wasNot called
        }
    }

    @Test
    fun signInInvokesSourceWithEmailAndPasswordArgs() = runTest {
        val expectedEmail = "email"
        val expectedPassword = "password"

        accountsRepository.signIn(expectedEmail,expectedPassword)

        coVerify {
            accountsSource.signIn(expectedEmail, expectedPassword)
        }
    }

    @Test
    fun signInWithFailOperationRethrowsException() = runTest {
        val expectedException = IllegalStateException("Some exception")
        coEvery { accountsSource.signIn(any(), any()) } throws expectedException

        val exception: IllegalStateException = catch {
            accountsRepository.signIn("email", "password")
        }

        assertSame(expectedException, exception)
    }


    @Test
    fun signInWithBackend401ExceptionThrowsInvalidCredentialsException() = runTest{
        coEvery { accountsSource.signIn(any(), any()) } throws BackendException(
            code = 401,
            message = "Auth error"
        )

        catch<InvalidCredentialsException> {
            accountsRepository.signIn("email", "password")
        }

        wellDone()
    }

    @Test
    fun signInWithSuccessfulOperationSavesToken() = runTest {
        val expectedToken = "current token"
        coEvery { accountsSource.signIn(any(), any()) } returns expectedToken

        accountsRepository.signIn("email", "password")

        verify(exactly = 1) {
            appSettings.setCurrentToken(expectedToken)
        }
    }

    @Test
    fun singInWithSuccessfulOperationNotifiesAboutSignedAccount()= runTest{
        val expectedAccount = createAccount()
        coEvery { accountsSource.signIn(any(), any()) } returns  "some-token"
        coEvery { accountsSource.getAccount() } returns expectedAccount


        accountsRepository.signIn("email", "password")

        coVerify(exactly = 1) {
            lazyFlowSubject.updateAllValues(refEq(expectedAccount))
        }
    }

    @Test
    fun signUpWithInvalidDataThrowsException() = runTest {
        val exceptionException = IllegalStateException("Some exception")
        val signUpData = mockk<SignUpData>()
        coEvery { signUpData.validate() } throws exceptionException

        val exception: IllegalStateException = catch {
            accountsRepository.signUp(signUpData)
        }

        assertSame(exceptionException, exception)
        coVerify {
            accountsSource wasNot called
        }
    }

    @Test
    fun signUpInvokesSourceWithSameArgs() = runTest {
        val signUpData = mockk<SignUpData>(relaxed = true)

        accountsRepository.signUp(signUpData)

        coVerify {
            accountsSource.signUp(refEq(signUpData))
        }
    }

    @Test
    fun signUpWithFailOperationRethrowsException() = runTest {
        val signUpData = mockk<SignUpData>(relaxed = true)
        val expectedException = IllegalStateException("Oops")
        coEvery { accountsSource.signUp(any())}  throws  expectedException

        val exception: IllegalStateException = catch {
            accountsRepository.signUp(signUpData)
        }

        assertSame(expectedException, exception)
    }

    @Test
    fun signUpWithBackend409ExceptionThrowsAccountAlreadyExistsException() = runTest {
        val signUpData = mockk<SignUpData>(relaxed = true)
        val expectedException = BackendException(
            code = 409,
            message = "Already exists"
        )
        coEvery { accountsSource.signUp(any()) } throws expectedException

        catch<AccountAlreadyExistsException> {
            accountsRepository.signUp(signUpData)
        }

        wellDone()
    }

    @Test
    fun signUpWithOtherBackendExceptionRethrowIt()  = runTest {
        val signUpData = mockk<SignUpData>(relaxed = true)
        val expectedException = BackendException(
            code = 400,
            message = "Some backend error message"
        )
        coEvery { accountsSource.signUp((any())) } throws expectedException

        val exception: BackendException = catch {
            accountsRepository.signUp(signUpData)
            }

        assertSame(expectedException, exception)
    }

    @Test
    fun reloadAccountTriggersFlowReloading(){
        val expectedFlow = mockk<Flow<Result<Account>>>()
        every { lazyFlowSubject.listen(Unit) } returns expectedFlow

        val flow = accountsRepository.getAccount()

        assertSame(expectedFlow, flow)
    }
    @Test
    fun updateAccountUserNameUpdatesUsernameAndNotificationAccountFlow() = runTest {
        val expectedNewUsername = "newUsername"
        val expectedNewAccount = createAccount(username = expectedNewUsername)
        coEvery { accountsSource.getAccount() } returns expectedNewAccount

        accountsRepository.updateAccountUsername(expectedNewUsername)

        coVerifyOrder {
            accountsSource.setUsername(expectedNewUsername)
            lazyFlowSubject.updateAllValues(expectedNewAccount)
        }
    }




    @Test
    fun logoutClearsTokenAndResetsAccountFlow(){
        arranged()

        accountsRepository.logout()

        verify {
            appSettings.setCurrentToken(null)
            lazyFlowSubject.updateAllValues(null)
        }
    }

    @Test
    fun updateAccountUsernameWithEmptyUsernameThrowsException() = runTest {
        arranged()

        val exception: EmptyFieldException = catch {
            accountsRepository.updateAccountUsername("   ")
        }

        assertEquals(Field.Username, exception.field)
    }

    @Test
    fun updateAccountUsernameWithFailedOperationRethrowsException() = runTest {
        val expectedException = IllegalStateException("Oops")
        coEvery { accountsSource.setUsername(any()) } throws expectedException

        val exception: IllegalStateException = catch {
            accountsRepository.updateAccountUsername("username")
        }

        assertSame(expectedException, exception)
    }

    @Test
    fun updateAccountUsernameWithFailedFetchAccountOperationException() = runTest {
        val expectedException = IllegalStateException("Oops again")
        coEvery { accountsSource.getAccount() } throws expectedException

        val exception: IllegalStateException = catch {
            accountsRepository.updateAccountUsername("username")
        }

        assertSame(expectedException, exception)
    }

    @Test
    fun updateAccountWithBackend401ExceptionOnUpdateOperationThrowsAuthException() = runTest{
        coEvery { accountsSource.setUsername(any()) } throws BackendException(
            code = 401,
            message = "Auth error"
        )

        catch<AuthException> {
            accountsRepository.updateAccountUsername("username")
        }

        wellDone()
    }
@Test
fun updateAccountUsernameWithBackend401ExceptionOnFetchAccountOperationThrowsAuthException() = runTest {
    coEvery { accountsSource.getAccount() } throws BackendException(
        code = 401,
        message = "Some auth error"
    )

    catch<AuthException> {
        accountsRepository.updateAccountUsername("username")
    }

    wellDone()
}
    @Test
    fun initLoadsCurrentAccount()= runTest {
        val expectedAccount = createAccount(id = 123)
        coEvery {accountsSource.getAccount()  } returns expectedAccount
        val slot = arrangeRepositoryWithLazyFlowSlot()

        val account = slot.captured.invoke(Unit)

        assertSame(expectedAccount, account)
    }


    @Test
    fun initWith404BackendExceptionThrowsAuthException() = runTest {
        coEvery { accountsSource.getAccount() } throws BackendException(
            code = 404,
            message = ""
        )
        val slot = arrangeRepositoryWithLazyFlowSlot()

      catch<AuthException> {
          slot.captured.invoke(Unit)
      }

        wellDone()
    }

    @Test
    fun initialWithoutBackendExceptionRethrowsIt() = runTest {
        val expectedException = BackendException(
            code = 400,
            message = "123"
        )
        coEvery { accountsSource.getAccount() } throws expectedException
        val slot = arrangeRepositoryWithLazyFlowSlot()

        val exception: BackendException = catch { slot.captured.invoke(Unit) }

        assertSame(expectedException, exception)
    }

    @Test
    fun initWithOtherExceptionRethrowsException() = runTest {
        val expectedException = IllegalStateException("Oops")
        coEvery { accountsSource.getAccount() } throws expectedException
        val slot = arrangeRepositoryWithLazyFlowSlot()

        val exception = catch<IllegalStateException> { slot.captured.invoke(Unit) }


        assertSame(expectedException,exception)
    }

   @Test
    fun initUsesDefaultBackendExceptionWrapper() = runTest {
        val expectedAccount = createAccount(id = 321)
            coEvery { wrapBackendExceptions<Account>(any()) } returns expectedAccount

            val slot =
                arrangeRepositoryWithLazyFlowSlot()

            val account = slot.captured.invoke(Unit)

            assertSame(expectedAccount, account)
            coVerify {
                accountsSource wasNot called
            }
    }



    private fun arrangeRepositoryWithLazyFlowSlot(): CapturingSlot<SuspendValueLoader<Unit, Account>> {
        val factory: LazyFlowFactory = mockk()
        val slot: CapturingSlot<SuspendValueLoader<Unit, Account>> = slot()
        every { factory.createLazyFlowSubject(capture(slot)) } returns mockk(relaxed = true)

        createRepository(lazyFlowFactory = factory)

        return slot
    }


    private fun createRepository(
        lazyFlowFactory: LazyFlowFactory = this.lazyFlowFactory
    ): AccountsRepository = AccountsRepository(
        accountSource = this.accountsSource,
        appSettings = this.appSettings,
        lazyFlowFactory = lazyFlowFactory
    )


}