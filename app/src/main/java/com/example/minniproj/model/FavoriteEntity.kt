package com.example.minniproj.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(

    @PrimaryKey
    val coverId: Int,

    val title: String,

    val author: String,

    val year: String
)