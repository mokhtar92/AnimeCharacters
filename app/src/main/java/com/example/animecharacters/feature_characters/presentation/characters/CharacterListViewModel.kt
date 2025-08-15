package com.example.animecharacters.feature_characters.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import com.example.animecharacters.feature_characters.domain.usecase.FetchCharacters
import com.example.animecharacters.feature_characters.domain.usecase.FilterCharacters
import com.example.animecharacters.feature_characters.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    fetchCharacters: FetchCharacters,
    private val filterCharacters: FilterCharacters,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow<CharacterListUiState>(CharacterListUiState.Loading)
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private val pagedCharacters: Flow<PagingData<CharacterModel>> = fetchCharacters()
        .cachedIn(viewModelScope)

    init {
        observeCharacters()
    }

    fun handleIntent(intent: CharacterListIntent) {
        when (intent) {
            is CharacterListIntent.SearchCharacter -> _query.value = intent.query
        }
    }

    private fun observeCharacters() {
        combine(
            flow = pagedCharacters,
            flow2 = _query
                .debounce(400)
                .distinctUntilChanged()
        ) { pagingData, query ->
            val filtered = filterCharacters(query, pagingData)
            filtered.map { it.toUiModel() }
        }.catch { e ->
            _uiState.value = CharacterListUiState.Error(e.message ?: "Unknown error")
        }.onEach { filteredUiModels ->
            _uiState.value = CharacterListUiState.Success(flowOf(filteredUiModels))
        }.launchIn(viewModelScope)
    }
}