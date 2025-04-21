package com.sdapps.auraascend.core.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EmotionEntity")
data class EmotionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = 0L,
    val date: String = "",
    val userInput: String ="",
    val userSelectedMood: String = "",
    val userSelectedCategories: String?,
    val predictedMood: String = ""
)

@Entity(tableName = "QuotesMaster")
data class QuotesMaster (
    @PrimaryKey val id : Int = 0,
    val quote : String = "",
    val author : String = ""
)
