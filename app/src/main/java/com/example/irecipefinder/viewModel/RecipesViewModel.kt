package com.example.irecipefinder.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irecipefinder.data.FavouritesManager
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.model.UiState
import com.example.irecipefinder.repository.RecipesRepository
import com.example.irecipefinder.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val favouritesManager: FavouritesManager,
    private val recipesRepository: RecipesRepository
) : ViewModel() {

    private val _filteredRecipes = MutableStateFlow(UiState<List<Recipe>>(data = emptyList()))
    val filteredRecipes: StateFlow<UiState<List<Recipe>>> = _filteredRecipes.asStateFlow()

    val favoriteRecipes: StateFlow<List<Recipe>> =
        favouritesManager.favorites.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _randomImage = MutableStateFlow(StringUtils.EMPTY_STRING)
    val randomImage: StateFlow<String> = _randomImage

    init {
        viewModelScope.launch {
            favouritesManager.favorites.collect { favoriteRecipes ->
                val favoriteNames =
                    favoriteRecipes.map { it.name }.toSet() // Extract names as Set<String>
                updateFavoriteRecipes(favoriteNames)
            }
        }
    }

    fun getRecipes(query: String) {
        _filteredRecipes.value = UiState(isLoading = true) // Show loading state

        viewModelScope.launch {
            recipesRepository.getRecipes(query)
                .collect { result ->
                    result.fold(
                        onSuccess = { recipes ->
                            _filteredRecipes.value = UiState(data = recipes, isLoading = false)
                        },
                        onFailure = { throwable ->
                            _filteredRecipes.value = UiState(
                                error = throwable.message ?: "Unknown error",
                                isLoading = false
                            )
                        }
                    )
                }
        }
    }

    fun isFavorite(recipeName: String): Boolean {
        return favoriteRecipes.value.any { it.name == recipeName }
    }

    fun setAsFavorite(recipe: Recipe) {
        favouritesManager.addFavorite(recipe)
    }

    fun removeAsFavorite(recipeName: String) {
        favouritesManager.removeFavorite(recipeName)
    }

    private fun updateFavoriteRecipes(favoriteNames: Set<String>) {
        viewModelScope.launch {
            val updatedFavorites = favoriteNames.mapNotNull { favName ->
                _filteredRecipes.value.data.orEmpty().find { it.name == favName }
                    ?: favouritesManager.getRecipeByName(favName)?.also {
                        Log.d("UpdateFavorites", "Loaded from persistence: $favName")
                    } ?: run {
                        Log.w("UpdateFavorites", "Recipe not found: $favName")
                        null
                    }
            }
            favouritesManager.saveFavorites(updatedFavorites)
        }
    }

    fun getFavoriteRecipes(): List<Recipe> {
        return favoriteRecipes.value
    }

    fun getImage() {
        viewModelScope.launch {
            recipesRepository.getImage().collect { result ->
                _randomImage.value = result
            }
        }
    }
}
