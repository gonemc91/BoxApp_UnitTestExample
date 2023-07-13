package com.example.http.app.model.accounts.entities

import com.example.http.app.model.EmptyFieldException
import com.example.http.app.model.Field
import com.example.http.app.model.PasswordMismatchException

/**
 * Field that should be provided during creating a new account.
 */
data class SignUpData(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
){
    /**
     * @throws EmptyFieldException
     * @throws PasswordMismatchException
     */

    fun validate(){
        if(email.isBlank()) throw EmptyFieldException(Field.Email)
        if(username.isBlank()) throw EmptyFieldException(Field.Username)
        if(password.isBlank()) throw EmptyFieldException(Field.Password)
        if(password != repeatPassword) throw PasswordMismatchException()
    }
}
