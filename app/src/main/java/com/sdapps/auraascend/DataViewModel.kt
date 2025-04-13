package com.sdapps.auraascend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdapps.auraascend.core.room.EmotionDao
import com.sdapps.auraascend.core.room.EmotionEntity
import com.sdapps.auraascend.view.home.RetrofitBuilder
import com.sdapps.auraascend.view.home.fragments.myday.ClassificationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class DataViewModel : ViewModel() {
    private val _onBoardingResponses = MutableLiveData<MutableMap<String, List<String>>>()
    val onBoardingResponses: LiveData<MutableMap<String, List<String>>> = _onBoardingResponses

    fun saveResponse(questionText: String, selectedOptions: List<String>) {
        val currentMap = _onBoardingResponses.value ?: mutableMapOf()
        currentMap[questionText] = selectedOptions
        _onBoardingResponses.value = currentMap
    }

    private val _selectedCategories =
        MutableLiveData<MutableList<Pair<Int, String>>>(mutableListOf())

    fun setReasonCategories(pair: Pair<Int, String>) {
        val currentList = _selectedCategories.value ?: mutableListOf()
        if (currentList.contains(pair)) {
            Log.d("CHIP", "deleted item : ${pair.first} : ${pair.second}")
            currentList.remove(pair)
        } else {
            Log.d("CHIP", "Added item : ${pair.first} : ${pair.second}")
            currentList.add(pair)
        }
        _selectedCategories.value = currentList
    }

    fun getReasonCategories(): MutableList<Pair<Int, String>>? {
        return _selectedCategories.value
    }

    fun getReasons(): String? {
        val reasonList = arrayListOf<String>()
        _selectedCategories.value?.map {
            reasonList.add(it.second)
        }
        return reasonList.joinToString(",")
    }


    private val _selectedEmotion = MutableLiveData<Int>()
    val selectedEmotion: LiveData<Int> = _selectedEmotion

    fun setEmotion(index: Int) {
        var currentEmotion = _selectedEmotion.value ?: 0
        currentEmotion = index
        _selectedEmotion.value = currentEmotion
    }

    fun getEmotionText(emotions: List<String>): String {
        return emotions[_selectedEmotion.value ?: 0]
    }

    private val _emotionLabel = MutableLiveData<String>()

    fun getEmotionLabel(): String? {
        return _emotionLabel.value
    }

    fun setEmotionLabel(label: String) {
        _emotionLabel.value = label
    }

    private val _quote = MutableLiveData<DayQuotes>()
    val quote: LiveData<DayQuotes> = _quote

    fun fetchQuote() {
        viewModelScope.launch {
            try {
                val result = RetrofitBuilder.api.getQuote()
                _quote.postValue(result)
            } catch (ex: Exception) {
                Log.e("QuoteVM", "Failed: ${ex.message}")
            }
        }
    }

    private val _allQuotes = MutableLiveData<ArrayList<DayQuotes>>()
    val allQuotes: LiveData<ArrayList<DayQuotes>> = _allQuotes

    fun fetchAllQuotes() {
        viewModelScope.launch {
            try {
                val result = RetrofitBuilder.api.getAllQuotes()
                _allQuotes.postValue(result)

            } catch (ex: kotlin.Exception) {
                ex.message
            }
        }
    }

    private val _emotionResult = MutableLiveData<String>()
    val emotionResult: LiveData<String> = _emotionResult

    fun predict(classifier: ClassificationModel, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = classifier.predictEmotion(text)
            _emotionResult.postValue(result)
        }
    }


    private val _moods = MutableStateFlow<List<EmotionEntity>>(emptyList())
    val moods: StateFlow<List<EmotionEntity>> get() = _moods

    fun insertIntoDB(emotion: EmotionEntity, dao: EmotionDao){
        viewModelScope.launch {
            dao.insertMood(emotion)
        }
    }

    fun getEmotionsFromDB(dao: EmotionDao, whereCondition: List<String>){
        viewModelScope.launch {
           _moods.value =  dao.getMoodsByLabels(whereCondition)
        }

    }

}


data class DayQuotes(
    val id: Int,
    val quote: String,
    val author: String
)