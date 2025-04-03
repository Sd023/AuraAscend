package com.sdapps.auraascend

import androidx.lifecycle.ViewModel

class DataViewModel: ViewModel() {
    val responses = mutableMapOf<Int, List<String>>()

    fun saveResponse(questionIndex: Int, selectedOptions: List<String>) {
        responses[questionIndex] = selectedOptions
    }
}