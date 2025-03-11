package com.example.irecipefinder.repository

import android.util.Log
import com.example.irecipefinder.data.OpenAIService
import com.example.irecipefinder.model.Message
import com.example.irecipefinder.model.OpenAIRequest
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.utils.RecipeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.Result.Companion.failure

class RecipesRepository @Inject constructor(
    @Named("RecipesService") private val recipesService: OpenAIService,
    @Named("ImagesService") private val imagesService: OpenAIService,
    private val recipeMapper: RecipeMapper
) {
    suspend fun getRecipes(input: String): Flow<Result<List<Recipe>>> = flow {
        try {
            val requestBody = OpenAIRequest(
                model = "meta-llama/Llama-3.3-70B-Instruct-Turbo",
                messages = listOf(
                    Message("system", "You are an AI that generates structured recipe responses."),
                    Message(
                        "user",
                        "Please generate $input recipes in the following format with valid image urls (use https://lorempixel.com for placeholders):: " +
                                "{\"name\": \"Spaghetti Carbonara\", \"duration\": 30, \"ingredients\": [\"Spaghetti\", \"Eggs\", \"Parmesan\", \"Pancetta\", \"Garlic\"], " +
                                "\"instructions\": [\"Cook the spaghetti according to the package instructions.\", \"Fry the pancetta until crispy.\", " +
                                "\"Whisk the eggs with the Parmesan cheese.\", \"Toss the spaghetti with the pancetta and egg mixture.\"], " +
                                "\"image_url\": \"https://gfs.com/wp-content/uploads/2023/06/790835-PWB-Gordon-Choice-Hot-Sauce.jpg\"}"
                    )
                )
            )

            val response = withContext(Dispatchers.IO) {
                recipesService.getRecipes(requestBody)
            }
            val recipes = recipeMapper.toRecipe(response)
            emit(Result.success(recipes))
        } catch (e: Exception) {
            Log.e("RecipesRepository", "Error fetching recipes", e)
            emit(failure(e))
        }
    }

    // api doesn't exist anymore
    suspend fun getImage(): Flow<String> = flow {
        try {
            val response = imagesService.getRandomImage()
            emit(response.image)
        } catch (e: Exception) {
            Log.e("RecipesRepository", "Error fetching image", e)
            emit(e.toString())
        }
    }
}