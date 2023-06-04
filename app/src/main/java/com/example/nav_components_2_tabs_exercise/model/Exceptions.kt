package com.example.nav_components_2_tabs_exercise.model

open class AppException: RuntimeException()

class EmptyFieldException(val field: Field): AppException()

class PasswordMismatchException : AppException()

class AccountAlreadyExistsException : AppException()

class AuthException: AppException()




