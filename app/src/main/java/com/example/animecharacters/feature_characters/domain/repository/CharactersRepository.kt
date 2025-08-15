package com.example.animecharacters.feature_characters.domain.repository

import androidx.paging.PagingData
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    val pageSize: Int
    fun getAnimeCharacters(): Flow<PagingData<CharacterModel>>
}