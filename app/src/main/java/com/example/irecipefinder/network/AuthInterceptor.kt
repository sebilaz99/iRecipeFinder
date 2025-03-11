package com.example.irecipefinder.network

import android.util.Log
import com.example.irecipefinder.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException("API Key is missing or invalid.")
        }
        try {
            val request = chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()
            return chain.proceed(request)
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Failed to add Authorization header: $e")
            throw e
        }
    }
}