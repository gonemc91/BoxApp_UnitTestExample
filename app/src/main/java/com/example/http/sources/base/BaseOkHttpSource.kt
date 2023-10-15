package com.example.http.sources.base

import com.example.http.app.model.BackendException
import com.example.http.app.model.ConnectionException
import com.example.http.app.model.ParseBackendResponseException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Base class for all OkHttp source.
 */
// todo #5: implement base source which uses OkHttp library for making HTTP requests
//          and GSON library for working with JSON data.
//          Implement the following methods:
//          - Call.suspendEnqueue() -> suspending function which should wrap OkHttp callbacks
//          - Request.Builder.endpoint() -> for concatenating Base URL and endpoint.
//          - T.toJsonRequestBody() -> for serializing any data class into JSON request body.
//          - Response.parseJsonResponse() -> for deserializing server responses into data classes.


open class BaseOkHttpSource(
    private val config: OkHttpConfig // all you need for build response
) {
    val gson: Gson = config.gson //for easy uses request field data class
    val  client: OkHttpClient = config.client//for easy uses request field data class

    val contentType = "application/json; charset=utf-8".toMediaType()
    /**
     * Suspending function which wraps OkHttp [Call.enqueue] method for making
     * HTTP requests and wraps external exceptions into subclasses of [AppException].
     *
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun Call.suspendEnqueue(): Response { //работа с корутинами(так как OkHttp не работает с ними из коробки)
        return suspendCancellableCoroutine { continuation -> //функция из корутин для преобразовния callback в корутины
            continuation.invokeOnCancellation {
                cancel() //вызывается когда корутина отменяется
            }
            enqueue(object : Callback {

               override fun onFailure(call: okhttp3.Call, e: IOException) {
                    val appException = ConnectionException(e) //создаем ошибку
                    continuation.resumeWithException(appException) //передаем далее ошибку
                }// ошибка соединения, метод выполняется в случае проблем с соединением ConnectionException

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    if (response.isSuccessful){ // провереям код ответа
                        continuation.resume(response) // передаем далее (использовать extension method)
                    }else{
                        handleErrorResponse(response, continuation)  // перехват BackendException ParseBackendResponseException (отдельный метод)
                    }
                } // получаем ответ от сервера
            })
        }
    }

    private fun handleErrorResponse(response: Response,
                                    continuation: CancellableContinuation<Response>){
        val httpCode = response.code // вытягиваем код ошибки
        try {// далее читаем тело ошибки через парсинг в интерфейс Map Coleecltions
            val map = gson.fromJson(response.body!!.string(), Map::class.java) // кого прочитать -> в какого превратить
            val message = map["error"].toString() // читаем поле error
            continuation.resumeWithException(BackendException(httpCode, message))
        }catch (e: Exception){// перехватываем то что есть
            val appException = ParseBackendResponseException(e)
            continuation.resumeWithException(appException)
        }
    }

    /**
     * Concatenate the base URL with a path and query args.
     */
    fun Request.Builder.endpoint(endpoint: String): Request.Builder {
        url("${config.baseUrl}$endpoint")
        return this
    }

    /**
     * Convert data class into [RequestBody] in JSON-format.
     */
    fun <T> T.toJsonRequestBody(): RequestBody {
        val json = gson.toJson(this)
        return json.toRequestBody(contentType)
    }

    /**
     * Parse OkHttp [Response] instance into data object. The type is derived from
     * TypeToken passed to this function as a second argument. Usually this method is
     * used to parse JSON arrays.
     *
     * @throws ParseBackendResponseException
     */
    fun <T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
        try {
            return gson.fromJson(this.body!!.string(), typeToken.type)
        }catch (e: Exception){
            throw ParseBackendResponseException(e)
        }

    }

    /**
     * Parse OkHttp [Response] instance into data object. The type is derived from
     * the generic type [T]. Usually this method is used to parse JSON objects.
     *
     * @throws ParseBackendResponseException
     */
    inline fun <reified T> Response.parseJsonResponse(): T {
        try {
            return gson.fromJson(this.body!!.string(), T::class.java)
        }catch (e: Exception){
            throw ParseBackendResponseException(e)
        }

    }

}



