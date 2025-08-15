package com.example.animecharacters.feature_characters.presentation.characters

import androidx.paging.PagingData
import com.example.animecharacters.feature_characters.presentation.model.CharacterUiModel
import kotlinx.coroutines.flow.Flow

sealed class CharacterListUiState {
    object Loading : CharacterListUiState()
    data class Success(val characters: Flow<PagingData<CharacterUiModel>>) : CharacterListUiState()
    data class Error(val message: String) : CharacterListUiState()
}