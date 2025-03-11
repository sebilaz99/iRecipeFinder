package com.example.irecipefinder.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val name: String,
    val duration: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageUrl: String? = null
)