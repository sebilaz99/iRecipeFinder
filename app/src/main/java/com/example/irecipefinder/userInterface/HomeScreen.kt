package com.example.irecipefinder.userInterface

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irecipefinder.R
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.model.UiState
import com.example.irecipefinder.ui.theme.Bold_32
import com.example.irecipefinder.ui.theme.Regular_16
import com.example.irecipefinder.utils.StringUtils
import com.example.irecipefinder.viewModel.RecipesViewModel

@Composable
fun HomeScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    onItemClicked: (Recipe) -> Unit
) {
    val context = LocalContext.current
    val filteredRecipes by viewModel.filteredRecipes.collectAsState()
    var query by remember { mutableStateOf(StringUtils.EMPTY_STRING) }
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    val randomImage by viewModel.randomImage.collectAsState()

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            viewModel.getRecipes(query)
        }
    }

    LaunchedEffect(favoriteRecipes) {
        viewModel.getFavoriteRecipes()
    }

    LaunchedEffect(Unit) {
        viewModel.getImage()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                SearchBar(
                    onSearch = {
                        if (query != it) {
                            query = it
                            viewModel.getRecipes(query)
                        }
                    },
                    hintText = context.getString(R.string.search_bar_hint)
                )
            }
        },
        content = { paddingValues ->
            val displayedRecipes =
                if (query.isNotBlank()) filteredRecipes else UiState(data = favoriteRecipes)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val title = if (query.isBlank()) {
                    stringResource(R.string.favorites_subtitle)
                } else {
                    stringResource(R.string.suggested_recipes_subtitle)
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = title,
                    style = Bold_32
                )

                VerticalSpacer(height = 10.dp)

                RecipesSection(
                    recipes = displayedRecipes,
                    image = randomImage,
                    query = query,
                    isFavorite = viewModel::isFavorite,
                    onFavouriteClick = { recipe ->
                        if (viewModel.isFavorite(recipe.name)) {
                            viewModel.removeAsFavorite(recipe.name)
                        } else {
                            viewModel.setAsFavorite(recipe)
                        }
                    },
                    onItemClicked = onItemClicked,
                    onButtonClicked = {
                        viewModel.getRecipes(query)
                    }
                )
                VerticalSpacer(16.dp)
            }
        }
    )
}

@Composable
fun RecipesSection(
    recipes: UiState<List<Recipe>>,
    image: String,
    query: String,
    isFavorite: (String) -> Boolean,
    onFavouriteClick: (Recipe) -> Unit,
    onItemClicked: (Recipe) -> Unit,
    onButtonClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when {
            recipes.isLoading -> {
                if (query.isNotBlank()) {
                    LoadingScreen()
                }
            }

            recipes.error != null -> {
                Log.d("RecipesSection", "error")
            }

            recipes.data != null -> {
                Log.d("RecipesSection", "${recipes.data}")
                recipes.data.let { list ->
                    if (list.isEmpty()) {
                        EmptyStateScreen()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(list.size) { index ->
                                val recipe = recipes.data[index]
                                RecipeItem(
                                    recipe = recipe,
                                    image = image,
                                    isFavorite = isFavorite,
                                    onFavouriteClick = onFavouriteClick,
                                    onItemClicked = onItemClicked
                                )
                                VerticalSpacer(10.dp)
                            }
                            item {
                                if (query.isNotBlank()) {
                                    VerticalSpacer(10.dp)
                                    IRFButton(
                                        modifier = Modifier
                                            .width(180.dp)
                                            .height(50.dp)
                                            .align(Alignment.CenterHorizontally),
                                        onClick = {
                                            onButtonClicked()
                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.refresh_button),
                                                style = Regular_16,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyStateScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No recipes found")
    }
}