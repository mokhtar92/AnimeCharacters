package com.example.animecharacters.feature_characters.data.mapper

import com.example.animecharacters.feature_characters.domain.model.CharacterPagingError
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.mapToDomainError(): CharacterPagingError = when (this) {
    is UnknownHostException -> CharacterPagingError.NoInternet
    is SocketTimeoutException -> CharacterPagingError.Timeout
    is HttpException -> when (code()) {
        404 -> CharacterPagingError.NotFound
        in 500..599 -> CharacterPagingError.ServerError
        else -> CharacterPagingError.Unknown(this)
    }

    else -> CharacterPagingError.Unknown(this)
}