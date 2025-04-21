package com.sdapps.auraascend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.sdapps.auraascend.core.room.AppDAO
import com.sdapps.auraascend.core.room.EmotionEntity
import com.sdapps.auraascend.core.room.QuotesMaster
import com.sdapps.auraascend.view.home.RetrofitBuilder
import com.sdapps.auraascend.view.home.fragments.myday.ClassificationModel
import com.sdapps.auraascend.view.home.spotify.FetchSong.fetchPodcasts
import com.sdapps.auraascend.view.home.spotify.PodcastShow
import com.sdapps.auraascend.view.login.data.UserBO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    private val _allQuotes = MutableLiveData<ArrayList<QuotesMaster>>()
    val allQuotes: LiveData<ArrayList<QuotesMaster>> = _allQuotes

    fun downloadAllQuotes(dao: AppDAO) {
        viewModelScope.launch {
            try {
                val result = RetrofitBuilder.api.getAllQuotes()
                for(data in result){
                    dao.insertQuotesMaster(data)
                }
            } catch (ex:Exception) {
                println(ex.message)
            }
        }
    }

    fun fetchQuotesFromDB(dao: AppDAO){
        viewModelScope.launch {
            val values = dao.getAllQuotes()
            _allQuotes.postValue(ArrayList(values))
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

    fun insertIntoDB(emotion: EmotionEntity, dao: AppDAO){
        viewModelScope.launch {
            dao.insertMood(emotion)
        }
    }

    fun getEmotionsFromDB(dao: AppDAO, whereCondition: List<String>){
        viewModelScope.launch {
           _moods.value =  dao.getMoodsByLabels(whereCondition)
        }
    }

    private val _dailyMoodData = MutableLiveData<Map<String, Int>>()
    val dailyMoodData: LiveData<Map<String, Int>> = _dailyMoodData

    private val moodLabelToInt = mapOf(
        "sadness" to 0,
        "joy" to 1,
        "love" to 2,
        "anger" to 3,
        "fear" to 4,
        "surprise" to 5
    )

    fun loadDominantMoods(dao: AppDAO, startDate: String, endDate: String) {
        viewModelScope.launch {
            val entries = dao.getEntriesBetween(startDate, endDate)
            val grouped: Map<String, Int> = entries
                .groupBy { it.date }
                .mapValues { (_, moods) ->
                    moods.groupingBy { moodLabelToInt[it.predictedMood] ?: -1 }
                        .eachCount()
                        .maxByOrNull { it.value }
                        ?.key ?: -1
                }

            _dailyMoodData.postValue(grouped)
        }
    }

    val podcastList = MutableLiveData<List<PodcastShow>?>()
    val errorMessage = MutableLiveData<String>()

    fun getPodcastList() : List<PodcastShow>? {
        return podcastList.value
    }
    fun getPodcasts(query: String, token: String){
        viewModelScope.launch(Dispatchers.IO) {
            fetchPodcasts(token, query) { podcasts ->
                if(podcasts != null){
                    podcastList.postValue(podcasts)
                } else {
                    errorMessage.postValue("No podcasts found")
                }
            }
        }
    }

    private val _allMoodEntries = MutableLiveData<List<EmotionEntity>>()
    val allMoodEntries: LiveData<List<EmotionEntity>> = _allMoodEntries

    fun getChartData(dao: AppDAO){
        viewModelScope.launch {
            _allMoodEntries.postValue(dao.getAllEntries())
        }
    }


    private val _profileBO = MutableStateFlow<UserBO>(UserBO())
    val profileBO: StateFlow<UserBO> get() = _profileBO

    fun fetchProfileConfigs(userId: String){
        viewModelScope.launch {
            //name,phone,dob
            Firebase.database.getReference("users").child(userId)
                .get().addOnSuccessListener { snapshot ->
                    val usr_name = snapshot.child("name").value.toString()
                    val usr_phone = snapshot.child("phoneNumber").value.toString()
                    val usr_email = snapshot.child("email").value.toString()
                    val usr_dob = snapshot.child("dateOfBirth").value.toString()

                    val bo = UserBO().apply {
                        userName = usr_name
                        phoneNumber = usr_phone
                        email = usr_email
                        dateOfBirth = usr_dob
                    }
                    _profileBO.value = bo
                }
        }
    }


}


data class DayQuotes(
    val id: Int,
    val quote: String,
    val author: String
)