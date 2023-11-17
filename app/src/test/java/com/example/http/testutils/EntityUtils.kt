package com.example.http.testutils

import android.graphics.Color
import com.example.http.domain.accounts.entities.Account
import com.example.http.domain.accounts.entities.SignUpData
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings


fun createAccount(
    id:Long = 1,
    username: String = "username",
    email: String = "email"
) =  Account(
    id = id,
    username = username,
    email = email,
    createdAt = Account.UNKNOWN_CREATED_AT
)


fun createSignUpData(
    username: String = "username",
    email: String = "email",
    password: String = "password",
    repeatPassword: String = "password"
) = SignUpData(
    username = username,
    email = email,
    password = password,
    repeatPassword = repeatPassword
)

fun createBox (
    id: Long = 2,
    colorName: String = "Red",
    colorValue: Int = Color.RED
) = Box(
    id =id,
    colorName = colorName,
    colorValue = colorValue
)

fun createBoxAndSettings(
    id: Long = 3,
    name: String = "Red",
    value: Int = Color.RED,
    isActive: Boolean = true

) = BoxAndSettings(
    Box(
        id = id,
        colorName = name,
        colorValue = value
    ),
    isActive = isActive
)