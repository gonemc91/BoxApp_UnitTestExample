package com.example.http.sources.base

import com.example.http.app.model.ParseBackendResponseException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit

class BaseRetrofitSource(
    retrofitConfig: RetrofitConfig
) {

    val retrofit: Retrofit = retrofitConfig.retrofit

    /**
     * Map network and parse exception into in-app exceptions.
     * @throws BackendException
     * @throws ParseBackendResponseException
     * @throws ConnectionException
     */


    suspend fun <T> wrapRetrofitExceptions (block: suspend () -> T): T{
        return try {
            block()
        }catch (e: JsonDataException){ // error Moshi wrapped
            throw ParseBackendResponseException(e)
        }catch (e: JsonEncodingException){// error Moshi wrapped
            throw ParseBackendResponseException(e)
        }catch (e: HttpException){//code note in 200
TODO()
        }catch (e: IOException){
TODO()
        }

        // execute 'block' passed to arguments and throw:
        // - BackendException with code and message if server has returned
        // - ParseBackendResponseException if server response can't be pars
        // -ConnectionException if HTTP request has failed

        TODO()
    }

    private  fun createBackendException (e: HttpException): Exception{
        TODO()
    }




}