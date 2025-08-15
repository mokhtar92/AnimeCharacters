package com.example.animecharacters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.animecharacters.feature_characters.presentation.characters.CharacterListIntent
import com.example.animecharacters.feature_characters.presentation.characters.CharacterListScreen
import com.example.animecharacters.feature_characters.presentation.characters.CharacterListViewModel
import com.example.animecharacters.navigation.Screen
import com.example.animecharacters.theme.AnimeCharactersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeCharactersTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.CharactersScreen.route,
                        modifier = Modifier.Companion.padding(innerPadding),
                    ) {
                        composable(Screen.CharactersScreen.route) {
                            val viewModel = hiltViewModel<CharacterListViewModel>()
                            val state by viewModel.uiState.collectAsStateWithLifecycle()

                            CharacterListScreen(
                                charactersUiState = state,
                                onSearchQueryChange = {
                                    viewModel.handleIntent(CharacterListIntent.SearchCharacter(it))
                                },
                                onCharacterClicked = {
                                    /*
                                        For a more complex object, this is safely done by passing only an identifier,
                                        then fetching it from the data source in the destination screen.
                                   */
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(Screen.CharactersScreen.KEY_CHARACTER, it)
                                    navController.navigate(Screen.CharacterDetailsScreen.route)
                                }
                            )
                        }

                        composable(Screen.CharacterDetailsScreen.route) {

                        }
                    }
                }
            }
        }
    }
}