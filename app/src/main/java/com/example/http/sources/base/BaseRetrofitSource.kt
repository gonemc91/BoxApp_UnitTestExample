package com.example.http.sources.base

import com.example.http.app.model.BackendException
import com.example.http.app.model.ConnectionException
import com.example.http.app.model.ParseBackendResponseException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit




// todo #4: add property for accessing Retrofit instance and implemented
// wrapRetrofitExceptions() method

open class BaseRetrofitSource(
    retrofitConfig: RetrofitConfig
) {

    val retrofit: Retrofit = retrofitConfig.retrofit //get retrofit

    private val errorAdapter =
        retrofitConfig.moshi.adapter(ErrorResponseBody::class.java) //error adapter for handle error

    /**
     * Map network and parse exception into in-app exceptions.
     * @throws BackendException
     * @throws ParseBackendResponseException
     * @throws ConnectionException
     */


    suspend fun <T> wrapRetrofitExceptions(block: suspend () -> T): T { // method for converting throw server in throw application
        return try {
            block()
        } catch (e: JsonDataException) { // error Moshi wrapped
            throw ParseBackendResponseException(e)
        } catch (e: JsonEncodingException) {// error Moshi wrapped  (heir IOException)
            throw ParseBackendResponseException(e)
        } catch (e: HttpException) {//code note in 200
            throw createBackendException(e) //parser exception on server
        } catch (e: IOException) {
            throw ConnectionException(e) // request could not be sent
        }

        // execute 'block' passed to arguments and throw:
        // - BackendException with code and message if server has returned
        // - ParseBackendResponseException if server response can't be pars
        // -ConnectionException if HTTP request has failed

        TODO()
    }

    private  fun createBackendException (e: HttpException): Exception{
        return try {
            val errorBody = errorAdapter.fromJson(
                e.response()!!.errorBody()!!.string()
            )!!
            BackendException(e.code(), errorBody.error) // create BackendException code + message

        }catch (e: Exception){
            throw ParseBackendResponseException(e) // !! NullPointerException handle this
        }
    }

    class ErrorResponseBody(
        val error: String
    )




}