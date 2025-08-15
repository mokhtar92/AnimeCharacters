package com.example.animecharacters.feature_characters.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.animecharacters.feature_characters.data.mapper.mapToDomainError
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import javax.inject.Inject

class CharacterPagingSource @Inject constructor(
    private val service: CharacterRemoteService
) : PagingSource<Int, CharacterModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val currentPage = params.key ?: 1

        return try {
            val response = service.getCharacters(mapOf("page" to "$currentPage"))
            val pagingInfo = response.info

            LoadResult.Page(
                data = response.results.map { it.toDomainModel() },
                prevKey = if (pagingInfo.hasPreviousPage) pagingInfo.previousPageNumber else null,
                nextKey = if (pagingInfo.hasNextPage) pagingInfo.nextPageNumber else null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load page $currentPage", e)
            LoadResult.Error(e.mapToDomainError())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    companion object {
        private val TAG = CharacterPagingSource::class.java.simpleName
    }
}