package com.example.http.sources

import com.example.http.app.Const
import com.example.http.app.Singletons
import com.example.http.app.model.SourcesProvider
import com.example.http.app.model.settings.AppSettings
import com.example.http.sources.base.RetrofitConfig
import com.example.http.sources.base.RetrofitSourcesProvider
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SourceProviderHolder {

    val sourcesProvider: SourcesProvider by lazy<SourcesProvider> {
        val moshi = Moshi.Builder().build()
        val config = RetrofitConfig(
            retrofit = createRetrofit(moshi),
            moshi=moshi
        )
        RetrofitSourcesProvider(config)

    }

    private fun createRetrofit(moshi: Moshi): Retrofit{
        return Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Create an instance of OkHttpClient with interceptor for authorization
     * and logging (see [createAuthorizationInterceptor] and [createLoggingInterceptor])
     */

    private fun createOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(createAuthorizationInterceptor(Singletons.appSettings))
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    /**
     * Add Authorization header to each request if JWT-token exists.
     */

    private fun createAuthorizationInterceptor(settings: AppSettings): Interceptor{
        return Interceptor{ chain ->
            val newBuilder = chain.request().newBuilder()
            val token = settings.getCurrentToken()
            if (token != null){
                newBuilder.addHeader("Authorization", token)
            }
            return@Interceptor chain.proceed(newBuilder.build())

        }
    }


    /**
     * Log request and response to LogCat
     */
    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

}