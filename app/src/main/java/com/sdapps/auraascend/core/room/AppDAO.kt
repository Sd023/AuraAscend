package com.sdapps.auraascend.core.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDAO {


    // EmotionEntity
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertMood(mood: EmotionEntity)

    @Query("SELECT * FROM EmotionEntity WHERE predictedMood IN (:moods)")
    suspend fun getMoodsByLabels(moods: List<String>): List<EmotionEntity>

    @Query("DELETE FROM EmotionEntity")
    suspend fun clearAll()

    @Query("SELECT * FROM EmotionEntity WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getEntriesBetween(startDate: String, endDate: String): List<EmotionEntity>

    @Query("SELECT * FROM EmotionEntity")
    suspend fun getAllEntries(): List<EmotionEntity>


    // QuotesMaster
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertQuotesMaster(quotesBO: QuotesMaster)

    @Query("SELECT * FROM QUOTESMASTER")
    suspend fun getAllQuotes(): List<QuotesMaster>
}