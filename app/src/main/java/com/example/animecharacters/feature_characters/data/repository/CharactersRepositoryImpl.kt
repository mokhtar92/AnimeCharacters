package com.example.animecharacters.feature_characters.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.animecharacters.feature_characters.data.remote.CharacterPagingSource
import com.example.animecharacters.feature_characters.data.remote.CharacterRemoteService
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import com.example.animecharacters.feature_characters.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val service: CharacterRemoteService
) : CharactersRepository {

    override val pageSize: Int
        get() = 20

    override fun getAnimeCharacters(): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { CharacterPagingSource(service) }
        ).flow
    }
}