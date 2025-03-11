package com.example.irecipefinder.data

import android.content.Context
import android.content.SharedPreferences
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.utils.StringUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FavouritesManager @Inject constructor(context: Context) {

    private val gson = Gson()
    private val key = "favorite_recipes"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(StringUtils.SHARED_PREFERENCES_REFERENCE, Context.MODE_PRIVATE)


    private val _favorites = MutableStateFlow(loadFavorites()) // Initialize with saved favorites
    val favorites: StateFlow<List<Recipe>> = _favorites

    fun saveFavorites(favorites: List<Recipe>) {
        val json = gson.toJson(favorites)
        sharedPreferences.edit().putString(key, json).apply()
        _favorites.value = favorites
    }

    private fun loadFavorites(): List<Recipe> {
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<Recipe>>() {}.type)
    }

    fun addFavorite(recipe: Recipe) {
        val currentFavorites = loadFavorites().toMutableList()
        if (currentFavorites.none { it.name == recipe.name }) { // Avoid duplicates
            currentFavorites.add(recipe)
            saveFavorites(currentFavorites)
        }
    }

    fun removeFavorite(recipeName: String) {
        val currentFavorites = loadFavorites().toMutableList()
        val updatedFavorites = currentFavorites.filterNot { it.name == recipeName }
        saveFavorites(updatedFavorites)
    }

    fun getRecipeByName(name: String): Recipe? {
        return favorites.value.find { it.name.equals(name, ignoreCase = true) }
    }

}