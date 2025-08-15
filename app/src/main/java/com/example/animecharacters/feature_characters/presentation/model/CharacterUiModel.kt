package com.example.animecharacters.feature_characters.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterUiModel(
    val id: Int,
    val name: String,
    val image: String,
    val species: String,
    val status: String,
) : Parcelable