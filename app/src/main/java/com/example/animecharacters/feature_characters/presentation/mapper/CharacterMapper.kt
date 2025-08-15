package com.example.animecharacters.feature_characters.presentation.mapper

import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import com.example.animecharacters.feature_characters.presentation.model.CharacterUiModel

fun CharacterModel.toUiModel(): CharacterUiModel = CharacterUiModel(
    id = id,
    name = name,
    image = image,
    species = species,
    status = status
)