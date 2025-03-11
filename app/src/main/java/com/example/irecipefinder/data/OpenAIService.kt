package com.example.irecipefinder.data

import com.example.irecipefinder.BuildConfig
import com.example.irecipefinder.model.ImageResponse
import com.example.irecipefinder.model.OpenAIRequest
import com.example.irecipefinder.model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("v1/chat/completions")
    suspend fun getRecipes(
        @Body requestBody: OpenAIRequest
    ): OpenAIResponse

    @GET("api/")
    suspend fun getRandomImage(): ImageResponse
}