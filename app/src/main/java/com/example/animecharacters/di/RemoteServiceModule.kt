package com.example.animecharacters.di

import com.example.animecharacters.feature_characters.data.remote.CharacterRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteServiceModule {

    @Provides
    @Singleton
    fun provideCharacterRemoteService(retrofit: Retrofit): CharacterRemoteService {
        return retrofit.create(CharacterRemoteService::class.java)
    }
}