package com.example.irecipefinder.utils

import android.util.Log
import com.example.irecipefinder.model.OpenAIResponse
import com.example.irecipefinder.model.Recipe
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import javax.inject.Inject

class RecipeMapper @Inject constructor(private val gson: Gson) {

    fun toRecipe(response: OpenAIResponse): List<Recipe> {
        val recipes = mutableListOf<Recipe>()

        response.choices.forEach { choice ->
            val recipeJson = choice.message.content

            Log.d("RecipeMapper", "Raw JSON Response: $recipeJson")

            val cleanRecipeJson = extractJsonContent(recipeJson)

            Log.d("RecipeMapper", "Cleaned JSON: $cleanRecipeJson")
            val recipe = parseRecipeJson(cleanRecipeJson)
            recipes.add(recipe)
        }

        return recipes
    }


    private fun extractJsonContent(recipeJson: String): String {
        val jsonStartIndex = recipeJson.indexOf("{")
        val jsonEndIndex = recipeJson.lastIndexOf("}") + 1

        return if (jsonStartIndex != -1 && jsonEndIndex != -1) {
            recipeJson.substring(jsonStartIndex, jsonEndIndex)
        } else {
            StringUtils.EMPTY_STRING
        }
    }

    private fun parseRecipeJson(recipeJson: String): Recipe {
        return try {
            gson.fromJson(recipeJson, Recipe::class.java)
        } catch (e: JsonSyntaxException) {
            Log.e("RecipeMapper", "Error parsing recipe JSON", e)
            Recipe("", 0, emptyList(), emptyList(), "")
        }
    }
}
