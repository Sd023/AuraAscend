package com.sdapps.auraascend.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sdapps.auraascend.view.home.fragments.funactivity.mindfulness.MindfulnessActivity.Companion.BREATHE

class SharedPrefHelper(context: Context) {
    private val spRef : SharedPreferences

    companion object {
        const val SP_NAME = "AA_PREFS"
        const val CURRENT_USER = "current_user"
        const val ONBOARD_COMPLETE = "onboard_complete"
        const val USER_TYPE = "user_type"

        const val HIGH_SCORE = "HIGH_SCORE"
        const val STORIES_READ = "STORES_READ"
        const val ALL_ACTIVITY = "ALL_ACTIVITY"
        const val STORES_COUNT = "STORIES_COUNT"

        const val LIKED_QUOTES = "liked_qoutes"

        const val PREDICTED_MOOD = "ml_mood"
    }
    init {
        spRef =  context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
    }

    fun getHighScore(): Int { return spRef.getInt(HIGH_SCORE,0) }
    fun getStoriesReadCount(): Int { return spRef.getInt(STORIES_READ, 0) }
    fun getQuotesRead(): Int { return spRef.getInt(LIKED_QUOTES, 0)}
    fun getAllActivites(): Int { return spRef.getInt(ALL_ACTIVITY, 0) }

    fun setPredictedMoodToSharedPref(mood: String){
        spRef.edit().putString(PREDICTED_MOOD,mood).apply()
    }

    fun getPredictedMoodFromSharedPref(): String? {
        return spRef.getString(PREDICTED_MOOD,"")
    }

    fun setQuotesLiked(count: Int){
        spRef.edit().putInt(LIKED_QUOTES,count).apply()
    }
    fun setAllActivity(ach: Int){
        spRef.edit().putInt(ALL_ACTIVITY,ach).apply()
    }

    fun getMindfulnessCount(): Int { return spRef.getInt(BREATHE,0) }

    fun setStoriesReadCount(count: Int){
        spRef.edit().putInt(STORIES_READ,count).apply()
    }
    fun setTotalStories(count : Int){
        spRef.edit().putInt(STORES_COUNT,count).apply()
    }
    fun getTotalStories(): Int{
        return spRef.getInt(STORES_COUNT,0)
    }
    fun setUserLoginType(type: String){
        spRef.edit().putString(USER_TYPE,type).apply()
    }

    fun getUserLoginType(): String {
        return spRef.getString(USER_TYPE,"").toString()
    }
    fun setCurrentUser(uid: String){
        spRef.edit().putString(CURRENT_USER, uid.toString()).apply()
    }

    fun setOnboardComplete(flag: Boolean){
        spRef.edit { putBoolean(ONBOARD_COMPLETE, flag) }
    }


    fun setOnBoardCompleteForUser(flag: Boolean){
        val currentUser = spRef.getString(CURRENT_USER,"").toString()
        if(currentUser.isNotEmpty()){
            spRef.edit().putBoolean(currentUser,flag).apply()
        }
    }

    fun getOnboardStatus(): Boolean {
        return spRef.getBoolean(ONBOARD_COMPLETE,false)
    }

    fun getUserOnboardStatus(): Boolean {
        val currentUser = getCurrentUser()
        return spRef.getBoolean(currentUser, false)
    }

    fun getCurrentUser(): String {
        return spRef.getString(CURRENT_USER,"").toString()
    }

    fun clearSp() {
        spRef.edit().clear().apply()
    }

}