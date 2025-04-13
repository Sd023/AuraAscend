package com.sdapps.auraascend.core.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "EmotionEntity")
data class EmotionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: String = "",
    val date: String = "",
    val userInput: String ="",
    val userSelectedMood: String = "",
    val userSelectedCategories: String?,
    val predictedMood: String = ""
)


@Dao
interface EmotionDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertMood(mood: EmotionEntity)

    @Query("SELECT * FROM EmotionEntity ORDER BY timestamp DESC")
    suspend fun getAllMoods(): List<EmotionEntity>

    @Query("SELECT * FROM EmotionEntity WHERE predictedMood IN (:moods)")
    suspend fun getMoodsByLabels(moods: List<String>): List<EmotionEntity>

    @Query("DELETE FROM EmotionEntity")
    suspend fun clearAll()
}
