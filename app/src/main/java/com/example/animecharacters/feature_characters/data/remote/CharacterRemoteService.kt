package com.example.animecharacters.feature_characters.data.remote

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CharacterRemoteService {
    @GET("character")
    suspend fun getCharacters(@QueryMap params: Map<String, String>): CharacterResponse
}