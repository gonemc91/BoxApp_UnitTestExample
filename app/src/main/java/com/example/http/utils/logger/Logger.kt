package com.example.http.utils.logger

interface Logger {

    fun log(tag: String, message: String)

    fun error(tag: String, e: Throwable)

}