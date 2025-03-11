package com.example.irecipefinder.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class OpenAIRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 300,
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("top_p") val topP: Double = 0.9
)