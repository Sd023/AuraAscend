package com.sdapps.auraascend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdapps.auraascend.view.home.RetrofitBuilder
import kotlinx.coroutines.launch
import java.lang.Exception

class DataViewModel: ViewModel() {
    private val _onBoardingResponses = MutableLiveData<MutableMap<String, List<String>>>()
    val onBoardingResponses : LiveData<MutableMap<String, List<String>>> = _onBoardingResponses

    fun saveResponse(questionText: String, selectedOptions: List<String>) {
        val currentMap = _onBoardingResponses.value ?: mutableMapOf()
        currentMap[questionText] = selectedOptions
        _onBoardingResponses.value = currentMap
    }

    private val _selectedCategories = MutableLiveData<MutableList<Pair<Int, String>>>(mutableListOf())
    val selectedCategories: LiveData<MutableList<Pair<Int, String>>> = _selectedCategories

    private val _reasonChipSelection = MutableLiveData<Pair<Int, String>>()
    val reasonChipSelection: LiveData<Pair<Int, String>> = _reasonChipSelection

    fun setReasonChip(pair: Pair<Int, String>) {
        val currentList = _selectedCategories.value ?: mutableListOf()

        if (currentList.contains(pair)) {
            Log.d("CHIP", "deleted item : ${pair.first} : ${pair.second}")
            currentList.remove(pair)
        } else {
            Log.d("CHIP", "Added item : ${pair.first} : ${pair.second}")
            currentList.add(pair)
        }

        _selectedCategories.value = currentList
        _reasonChipSelection.value = pair
    }

    private val _selectedEmotion = MutableLiveData<Int>()
    val selectedEmotion : LiveData<Int> = _selectedEmotion

    fun setEmotion(index: Int){
        _selectedEmotion.value = index
    }

    fun getEmotionText(emotions: List<String>): String {
        return emotions.get(_selectedEmotion.value ?: 0)
    }

    private val _quote = MutableLiveData<DayQuotes>()
    val quote: LiveData<DayQuotes> = _quote

    fun fetchQuote(){
        viewModelScope.launch {
            try {
                val result = RetrofitBuilder.api.getQuote()
                _quote.postValue(result)
            }catch (ex: Exception){
                Log.e("QuoteVM", "Failed: ${ex.message}")
            }
        }
    }

    private val _allQuotes = MutableLiveData<ArrayList<DayQuotes>>()
    val allQuotes: LiveData<ArrayList<DayQuotes>> = _allQuotes

    fun fetchAllQuotes(){
        viewModelScope.launch {
            try {
                val result = RetrofitBuilder.api.getAllQuotes()
                _allQuotes.postValue(result)

            }catch (ex:kotlin.Exception){
                ex.message
            }
        }
    }



}


data class DayQuotes(
    val id : Int,
    val quote : String,
    val author : String
)