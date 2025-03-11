package com.example.irecipefinder.userInterface

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irecipefinder.R
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.ui.theme.Bold_16
import com.example.irecipefinder.ui.theme.Bold_24
import com.example.irecipefinder.ui.theme.Regular_14
import com.example.irecipefinder.ui.theme.Regular_16
import com.example.irecipefinder.utils.StringUtils
import com.example.irecipefinder.viewModel.RecipesViewModel

@Composable
fun RecipeDetailsScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    onBack: () -> Unit,
    recipe: Recipe,
) {
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    val isFavorite = favoriteRecipes.any { it.name == recipe.name }

        Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.recipe_placeholder),
                contentDescription = StringUtils.RECIPE_PLACEHOLDER,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopStart),
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_left_arrow),
                    tint = Color.Black,
                    contentDescription = StringUtils.BACK_BUTTON
                )
            }
        }

        Log.d("RecipeDetailsScreen", "Displaying details for: ${recipe.name}")

        DetailsSection(
            recipe = recipe,
            isFavorite = isFavorite,
            onFavouriteClick = {
                if (isFavorite) {
                    viewModel.removeAsFavorite(recipe.name)
                } else {
                    viewModel.setAsFavorite(recipe)
                }
            }
        )
    }
}

@Composable
fun DetailsSection(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavouriteClick: (Recipe) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Added horizontal padding
    ) {
        item {
            VerticalSpacer(16.dp)

            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recipe.name,
                        style = Bold_24,
                        modifier = Modifier.fillMaxWidth()
                    )
                    VerticalSpacer(5.dp)
                    Text(
                        text = stringResource(R.string.recipe_duration, recipe.duration),
                        style = Regular_14,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    )
                }
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { onFavouriteClick(recipe) }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFavorite) R.drawable.ic_heart_filled
                            else R.drawable.ic_heart_unfilled
                        ),
                        tint = colorResource(R.color.purple),
                        contentDescription = StringUtils.FAVOURITE_RECIPE_BUTTON
                    )
                }
            }

            VerticalSpacer(16.dp)

            if (recipe.ingredients.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.ingredients),
                    modifier = Modifier.fillMaxWidth(),
                    style = Bold_16,
                )
                VerticalSpacer(8.dp)
                IngredientsSection(ingredients = recipe.ingredients)
                VerticalSpacer(16.dp)
            }

            if (recipe.instructions.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.instructions),
                    modifier = Modifier.fillMaxWidth(),
                    style = Bold_16,
                )
                VerticalSpacer(8.dp)
                InstructionsSection(instructions = recipe.instructions)
                VerticalSpacer(16.dp)
            }
            VerticalSpacer(16.dp)
        }
    }
}

@Composable
fun IngredientsSection(ingredients: List<String>) {
    for (ingredient in ingredients) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .size(4.dp)
                    .align(Alignment.Top)
                    .offset(y = 12.dp)
            ) {
                drawCircle(color = Color.Black)
            }

            Text(
                text = ingredient,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.Top)
            )
        }
    }
}

@Composable
fun InstructionsSection(instructions: List<String>) {
    for (index in instructions.indices) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "${index + 1}. ",
                style = Regular_16,
            )
            Text(
                text = instructions[index],
                modifier = Modifier.fillMaxWidth(),
                style = Regular_16,
            )
        }
    }
}
