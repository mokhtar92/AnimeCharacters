package com.example.animecharacters.feature_characters.presentation.characters

sealed class CharacterListIntent {
    data class SearchCharacter(val query: String) : CharacterListIntent()
}