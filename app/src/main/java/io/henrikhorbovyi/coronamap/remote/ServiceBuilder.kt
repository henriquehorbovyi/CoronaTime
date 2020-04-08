package io.henrikhorbovyi.coronamap.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

interface ServiceBuilder {

    companion object {
        inline operator fun <reified S> invoke(baseUrl: String, authToken: String? = ""): S {
            val httpClient = buildInterceptors()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(S::class.java)
        }

        fun buildInterceptors(): OkHttpClient {
            val loggerInterceptor = getLoggerInterceptor()

            return OkHttpClient.Builder().apply {
                addInterceptor(loggerInterceptor)
            }.build()
        }

        private fun getLoggerInterceptor(): HttpLoggingInterceptor {
            val level = HttpLoggingInterceptor.Level.BODY

            /* = if (BuildConfig.DEBUG)
               HttpLoggingInterceptor.Level.BODY
           else
               HttpLoggingInterceptor.Level.NONE*/
            return HttpLoggingInterceptor().apply { this.level = level }
        }
    }
}