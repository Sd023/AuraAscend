package com.sdapps.auraascend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdapps.auraascend.view.home.QuoteAPI
import com.sdapps.auraascend.view.home.RetrofitBuilder
import kotlinx.coroutines.launch
import java.lang.Exception

class DataViewModel: ViewModel() {
    val responses = mutableMapOf<Int, List<String>>()

    fun saveResponse(questionIndex: Int, selectedOptions: List<String>) {
        responses[questionIndex] = selectedOptions
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



}


data class DayQuotes(
    val id : Int,
    val quote : String,
    val author : String
)