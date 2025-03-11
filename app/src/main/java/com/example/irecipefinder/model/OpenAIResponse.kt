package com.example.irecipefinder.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponse(val choices: List<Choice>)

@Serializable
data class Choice(val message: Message)

@Serializable
data class Message(
    val role: String,
    val content: String
)
