package com.example.animecharacters.navigation

sealed class Screen(
    val route: String,
) {
    data object CharactersScreen : Screen("characters_screen") {
        const val KEY_CHARACTER = "KEY_CHARACTER"
    }

    data object CharacterDetailsScreen : Screen("character_details_screen")
}