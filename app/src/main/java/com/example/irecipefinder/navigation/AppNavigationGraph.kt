package com.example.irecipefinder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.userInterface.HomeScreen
import com.example.irecipefinder.userInterface.RecipeDetailsScreen
import com.example.irecipefinder.utils.StringUtils
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    val navActions = remember(navController) { AppNavigationActions(navController) }
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onItemClicked = { recipe ->
                navActions.goToDetailsScreen(recipe)
            })
        }

        composable("recipeDetails/{recipe}") { backStackEntry ->
            val recipeJson = backStackEntry.arguments?.getString("recipe")
            val recipe = Json.decodeFromString<Recipe>(recipeJson!!)
            RecipeDetailsScreen(recipe = recipe, onBack = { navActions.goBack() })
        }
    }
}

class AppNavigationActions(private val navController: NavHostController) {
    fun goToDetailsScreen(recipe: Recipe) =
        navController.navigate("recipeDetails/${Json.encodeToJsonElement(recipe)}")

    fun goBack() {
        navController.popBackStack()
    }
}

enum class Screen(val route: String) {
    Home(StringUtils.HOME_ROUTE),
    RecipeDetails("details/{recipeName}")
}