package com.example.animecharacters.di

import com.example.animecharacters.feature_characters.data.repository.CharactersRepositoryImpl
import com.example.animecharacters.feature_characters.domain.repository.CharactersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideCharacterRepository(charactersRepositoryImpl: CharactersRepositoryImpl): CharactersRepository
}