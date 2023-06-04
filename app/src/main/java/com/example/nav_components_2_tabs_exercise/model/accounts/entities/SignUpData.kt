package com.example.nav_components_2_tabs_exercise.model.accounts.entities

import com.example.nav_components_2_tabs_exercise.model.EmptyFieldException
import com.example.nav_components_2_tabs_exercise.model.Field
import com.example.nav_components_2_tabs_exercise.model.PasswordMismatchException

data class SignUpData(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
){
    fun validate(){
        if(email.isBlank()) throw EmptyFieldException(Field.Email)
        if(username.isBlank()) throw EmptyFieldException(Field.Username)
        if(password.isBlank()) throw EmptyFieldException(Field.Password)
        if(password != repeatPassword) throw PasswordMismatchException()
    }
}
