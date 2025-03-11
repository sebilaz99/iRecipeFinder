package com.example.irecipefinder.model

data class UiState<out T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)