package com.example.http.domain.accounts.entities

import com.example.http.domain.EmptyFieldException
import com.example.http.domain.Field
import com.example.http.domain.PasswordMismatchException
import com.example.http.testutils.catch
import com.example.http.testutils.createSignUpData
import com.example.http.testutils.wellDone
import org.junit.Assert.assertEquals
import org.junit.Test

class SignUpDataTest {


    @Test
    fun validateForBlankEmailThrowsException(){
        val signUpData = createSignUpData( email = "    ")

        val exception: EmptyFieldException = catch { signUpData.validate() }

        assertEquals(Field.Email, exception.field)

    }

    @Test
    fun validateForBlankUsernameThrowsException(){
        val signUpData = createSignUpData( username = "    ")

        val exception: EmptyFieldException = catch { signUpData.validate() }

        assertEquals(Field.Username, exception.field)

    }



    @Test
    fun validateForBlankPasswordThrowsException(){
        val signUpData = createSignUpData( password = "    ")

        val exception: EmptyFieldException = catch { signUpData.validate() }

        assertEquals(Field.Password, exception.field)

    }


    @Test
    fun validateForMismatchPasswordThrowsException(){
        val signUpData = createSignUpData(
            password = "p1",
            repeatPassword = "p2")

        catch<PasswordMismatchException> { signUpData.validate() }

        wellDone()
    }

    @Test
    fun validateForValidDateDoesNothing(){
        val signUpData = createSignUpData()

        signUpData.validate()

        wellDone()
    }


}