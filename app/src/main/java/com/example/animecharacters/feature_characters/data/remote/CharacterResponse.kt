package com.example.animecharacters.feature_characters.data.remote

import androidx.core.net.toUri
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("info") val info: Info,
    @SerializedName("results") val results: List<Character>
)

data class Info(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("prev") val prev: String?
) {
    val nextPageNumber: Int?
        get() = extractPageNumber(next)

    val hasNextPage: Boolean
        get() = nextPageNumber != null

    val previousPageNumber: Int?
        get() = extractPageNumber(prev)

    val hasPreviousPage: Boolean
        get() = previousPageNumber != null


    private fun extractPageNumber(url: String?): Int? {
        if (url == null) return null

        return runCatching {
            val uri = url.toUri()
            uri.getQueryParameter("page")?.toIntOrNull()
        }.getOrNull()
    }
}

data class Character(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("species") val species: String?,
    @SerializedName("status") val status: String?,
) {

    fun toDomainModel(): CharacterModel {
        return CharacterModel(
            id = id,
            name = name ?: "N/A",
            image = image ?: "",
            species = species ?: "N/A",
            status = status ?: "N/A"
        )
    }
}