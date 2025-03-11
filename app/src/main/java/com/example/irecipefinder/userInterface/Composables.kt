package com.example.irecipefinder.userInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.irecipefinder.R
import com.example.irecipefinder.model.Recipe
import com.example.irecipefinder.ui.theme.Bold_16
import com.example.irecipefinder.ui.theme.Regular_14
import com.example.irecipefinder.utils.StringUtils

@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {},
    page: @Composable (LazyListState) -> Unit,
    loading: @Composable BoxScope.() -> Unit = { CircularProgressIndicator() },
    isContentLoading: Boolean = false,
    isContentScrollable: Boolean = true,
    verticalPageArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalPageAlignment: Alignment.Horizontal = Alignment.Start,
    horizontalPadding: Dp = 16.dp
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(modifier = modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .zIndex(3f)
            ) {
                header()
            }
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                        .zIndex(1f),
                    contentPadding = innerPadding,
                    state = lazyListState,
                    userScrollEnabled = isContentScrollable,
                    horizontalAlignment = horizontalPageAlignment,
                    verticalArrangement = verticalPageArrangement
                ) {
                    item {
                        page.invoke(lazyListState)
                    }
                }
            }
        }
    }
    if (isContentLoading) {
        Box(
            modifier = Modifier
                .zIndex(3f)
                .fillMaxSize()
                .alpha(.9f)
                .background(Color.White)
                .pointerInput(Unit) {}
        ) {
            loading()
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    image: String,
    isFavorite: (String) -> Boolean,
    onFavouriteClick: (Recipe) -> Unit,
    onItemClicked: (Recipe) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onItemClicked(recipe) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val painter =
                    rememberAsyncImagePainter(model = if (recipe.imageUrl?.isEmpty() == true) image else recipe.imageUrl)
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = recipe.name,
                    style = Bold_16,
                    color = Color.Black
                )
                Text(
                    text = stringResource(R.string.recipe_duration, recipe.duration),
                    style = Regular_14,
                    color = Color.Black
                )
            }

            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { onFavouriteClick(recipe) }) {
                Icon(
                    painter = painterResource(
                        id = if (isFavorite(recipe.name)) R.drawable.ic_heart_filled
                        else R.drawable.ic_heart_unfilled
                    ),
                    contentDescription = "Favorite",
                    tint = if (isFavorite(recipe.name)) colorResource(R.color.purple) else Color.Black
                )
            }
        }
    }
}


@Composable
fun SearchBar(onSearch: (String) -> Unit, hintText: String) {
    var query by remember { mutableStateOf(StringUtils.EMPTY_STRING) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(40.dp)
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            textStyle = TextStyle(fontSize = 16.sp),
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, MaterialTheme.shapes.medium),
            placeholder = { Text(hintText) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = colorResource(R.color.text_hint_color),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun IRFButton(modifier: Modifier, onClick: () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.purple)),
        shape = RoundedCornerShape(10.dp)
    ) {
        content()
    }
}

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}