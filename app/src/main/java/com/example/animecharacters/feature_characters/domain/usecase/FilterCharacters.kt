package com.example.animecharacters.feature_characters.domain.usecase

import androidx.paging.PagingData
import androidx.paging.filter
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import javax.inject.Inject

class FilterCharacters @Inject constructor() {

    operator fun invoke(
        query: String,
        characters: PagingData<CharacterModel>,
        criteria: ((CharacterModel) -> Boolean)? = null
    ): PagingData<CharacterModel> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return characters

        val effectiveCriteria = criteria ?: { it.name.contains(trimmedQuery, ignoreCase = true) }

        return characters.filter(effectiveCriteria)
    }
}