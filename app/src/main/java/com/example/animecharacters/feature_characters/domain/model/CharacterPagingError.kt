package com.example.animecharacters.feature_characters.domain.model

sealed class CharacterPagingError : Throwable() {
    object NoInternet : CharacterPagingError() {
        @JvmStatic
        private fun readResolve(): Any = NoInternet
    }

    object Timeout : CharacterPagingError() {
        @JvmStatic
        private fun readResolve(): Any = Timeout
    }

    object NotFound : CharacterPagingError() {
        @JvmStatic
        private fun readResolve(): Any = NotFound
    }

    object ServerError : CharacterPagingError() {
        @JvmStatic
        private fun readResolve(): Any = ServerError
    }

    data class Unknown(val error: Throwable) : CharacterPagingError()
}