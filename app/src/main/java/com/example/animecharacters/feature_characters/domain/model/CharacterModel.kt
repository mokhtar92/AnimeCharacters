package com.example.animecharacters.feature_characters.domain.model

data class CharacterModel(
    val id: Int,
    val name: String,
    val image: String,
    val species: String,
    val status: String,
)