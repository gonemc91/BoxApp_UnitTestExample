package com.example.http

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor


data class SignInRequestBody(
    val email: String,
    val password: String
) // тело запроса

data class SignInResponseBody(
    val token: String
) //дата класс с возваращаемыми данными с полем токен

val contentType = "application/json; charset=utf-8".toMediaType()

fun main() {
    val loggingInterceptor = HttpLoggingInterceptor() // перерхватчик логирования
        .setLevel(HttpLoggingInterceptor.Level.BODY) //параметр с максимальным уровнем

    val gson = Gson() //создание объекта парсера

    val client = OkHttpClient.Builder() // построитель клиента
        .addInterceptor(loggingInterceptor)  //добавляем перехватчик
        .build() // команда создания

    val requestBody = SignInRequestBody(
        email = "admin@google.com",
        password = "123"
    ) // создаем экземляр класса

    val requestBodyString = gson.toJson(requestBody) //переводим запрос в JSON запрос
    val okHttpRequestBody = requestBodyString.toRequestBody(contentType) //тело запроса с указанием типа контента

    val request = Request.Builder() // создаем шаблон запроса
        .post(okHttpRequestBody) //тип запроса с телом запроса
        .url("http://127.0.0.1:12345/sign-in")  //указание урл с типом
        .build() //комнада создания

    val call = client.newCall(request) //создание запроса с использованием клиента

    val response = call.execute() // синхронный вызов на главном потоке (enque для асинхронного вызова)

    if (response.isSuccessful) { //проверка на код ответа от 200 до 299
        val responseBodyString = response.body!!.string() //получаем ответ и переводим его к типу стринг (не равен NULL)
        val signInResponseBody = gson.fromJson(
            responseBodyString, //строка Json
            SignInResponseBody::class.java) //во что должны превратить data class with field token
        println("TOKEN: ${signInResponseBody.token}") //вывод на экран

    } else {
        throw IllegalStateException("Oops") // не подходящие состояние во время запроса
    }

}



