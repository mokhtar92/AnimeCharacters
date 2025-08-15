package com.example.animecharacters.feature_characters.domain.usecase

import androidx.paging.PagingData
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import com.example.animecharacters.feature_characters.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchCharacters @Inject constructor(
    private val repository: CharactersRepository
) {

    operator fun invoke(): Flow<PagingData<CharacterModel>> {
        return repository.getAnimeCharacters()
    }
}