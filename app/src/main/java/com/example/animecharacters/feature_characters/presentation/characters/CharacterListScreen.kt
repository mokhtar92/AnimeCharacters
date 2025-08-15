@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.animecharacters.feature_characters.presentation.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.animecharacters.R
import com.example.animecharacters.feature_characters.domain.model.CharacterPagingError
import com.example.animecharacters.feature_characters.presentation.model.CharacterUiModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun CharacterListScreen(
    charactersUiState: CharacterListUiState,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClicked: (CharacterUiModel) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Surface {
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                placeholder = { Text(stringResource(R.string.search)) },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = charactersUiState) {
                    is CharacterListUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is CharacterListUiState.Error -> {
                        ErrorState(message = state.message)
                    }

                    is CharacterListUiState.Success -> Column {
                        val characters = state.characters.collectAsLazyPagingItems()

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(characters.itemCount) { index ->
                                characters[index]?.let { character ->
                                    CharacterItem(
                                        character = character,
                                        onClick = { onCharacterClicked(character) }
                                    )
                                }
                            }

                            // Refresh Error
                            when (val refreshState = characters.loadState.refresh) {
                                is LoadState.Error -> {
                                    item(span = { GridItemSpan(2) }) {
                                        ErrorState(
                                            message = getErrorMessage(refreshState.error),
                                            onRetry = { characters.retry() }
                                        )
                                    }
                                }

                                else -> Unit
                            }

                            // Append (pagination) states
                            when (val appendState = characters.loadState.append) {
                                is LoadState.Loading -> {
                                    item(span = { GridItemSpan(2) }) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                is LoadState.Error -> {
                                    item(span = { GridItemSpan(2) }) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = getErrorMessage(appendState.error),
                                                color = MaterialTheme.colorScheme.error,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(onClick = { characters.retry() }) {
                                                Text(stringResource(R.string.retry_loading_more))
                                            }
                                        }
                                    }
                                }

                                else -> Unit
                            }

                            // Empty search result
                            if (characters.itemCount == 0 &&
                                searchQuery.isNotBlank() &&
                                characters.loadState.append !is LoadState.Loading
                            ) {
                                item(span = { GridItemSpan(2) }) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 64.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_characters_found),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun CharacterItem(
    character: CharacterUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = character.species,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun getErrorMessage(error: Throwable): String {
    return when (error as? CharacterPagingError) {
        is CharacterPagingError.NoInternet -> stringResource(R.string.please_check_your_internet_connection_and_retry)

        is CharacterPagingError.Timeout -> stringResource(R.string.request_timed_out)

        is CharacterPagingError.NotFound -> stringResource(R.string.characters_not_found)

        is CharacterPagingError.ServerError -> stringResource(R.string.server_error_try_again_later)

        else -> stringResource(R.string.oops_something_went_wrong_please_try_again_later)
    }
}

@Preview
@Composable
private fun CharacterListScreenSuccessPreview() {
    val characters = listOf(
        CharacterUiModel(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            status = "Alive",
            image = ""
        ),
        CharacterUiModel(
            id = 2,
            name = "Morty Smith",
            species = "Human",
            status = "Alive",
            image = ""
        )
    )

    val state = CharacterListUiState.Success(flowOf(PagingData.from(characters)))

    CharacterListScreen(
        charactersUiState = state,
        {},
        {},
    )
}

@Preview
@Composable
private fun CharacterListScreenErrorPreview() {
    val state = CharacterListUiState.Error("Check internet connection!")
    CharacterListScreen(
        charactersUiState = state,
        {},
        {},
    )
}

@Preview
@Composable
private fun CharacterListScreenLoadingPreview() {
    val state = CharacterListUiState.Loading
    CharacterListScreen(
        charactersUiState = state,
        {},
        {},
    )
}

@Preview
@Composable
private fun CharacterItemPreview() {
    CharacterItem(
        character = CharacterUiModel(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            status = "Alive",
            image = ""
        ),
        onClick = {}
    )
}