package com.sdapps.auraascend.view.home.fragments.myday

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.core.room.AppDatabase
import com.sdapps.auraascend.core.room.EmotionDao
import com.sdapps.auraascend.core.room.EmotionEntity
import com.sdapps.auraascend.databinding.ActivityLogBinding
import com.sdapps.auraascend.view.home.fragments.myday.DayFragment.Companion.HAPPY
import com.sdapps.auraascend.view.home.fragments.myday.DayFragment.Companion.NEUTRAL
import com.sdapps.auraascend.view.home.fragments.myday.DayFragment.Companion.ROUGH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogActivity : AppCompatActivity() {

    private lateinit var spRef : SharedPrefHelper
    private lateinit var binding: ActivityLogBinding

    private val categoryToLabels = hashMapOf<String, List<String>>(
        HAPPY to listOf("joy", "love"),
        NEUTRAL to listOf("surprise","love"),
        ROUGH to listOf("sadness", "anger", "fear")
    )

    private lateinit var appDatabase: AppDatabase
    private lateinit var dao: EmotionDao
    private lateinit var viewModel: DataViewModel
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getAliens = intent.extras
        val moduleCode = getAliens?.getString("moduleCode") ?: ""
        Log.d("Log", moduleCode)
        spRef = SharedPrefHelper(this)
        progressDialog = CustomProgressDialog(this)
        init(moduleCode)
    }

    private fun init(moduleCode : String){
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        appDatabase = AppDatabase.getDatabase(this)
        dao = appDatabase.getAppDAO()

        progressDialog.showLoadingProgress("Fetching Data...")
        CoroutineScope(Dispatchers.IO).launch {

            val queryFromFirebase = async { getDataFromFireBase(moduleCode) }
            queryFromFirebase.await()

            val queryFromDB = async { getDataFromFireBase(moduleCode) }
            queryFromDB.await()
        }
    }

    private fun setupView(moodList: List<EmotionEntity>){
        binding.logRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val myAdapter = LogAdapter(moodList,this)
        binding.logRV.adapter = myAdapter
    }

    fun getDataFromFireBase(moduleCode: String){
        try {
            val moodLabels = categoryToLabels[moduleCode] as List<String>
            viewModel.getEmotionsFromDB(dao,moodLabels)

            lifecycleScope.launch {
                viewModel.moods.collect { moodList ->
                    //
                }
            }

            Log.d("Log", " Module code is : $moduleCode")
            Firebase.firestore
                .collection("users")
                .document(spRef.getCurrentUser())
                .collection("mood_history")
                .whereIn("predictedMood", moodLabels)
                .get()
                .addOnSuccessListener { snapshot ->
                    Log.d("Log", "Document size : ${snapshot.documents.size}")
                    val emotionList = snapshot.documents.mapNotNull { it.toEmotionEntity() }
                    CoroutineScope(Dispatchers.Main).launch {
                        progressDialog.closePialog()
                        setupView(emotionList)
                    }

                }
                .addOnFailureListener { err ->
                    Log.e("Error", "Failed to fetch: ${err.message}")
                }


        } catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    fun DocumentSnapshot.toEmotionEntity(): EmotionEntity? {
        return try {
            val date = getString("date") ?: ""
            val userInput = getString("userInput") ?: ""
            val userSelectedMood = getString("userSelectedMood") ?: ""
            val predictedMood = getString("predictedMood") ?: ""
            val timestamp = getTimestamp("timestamp")?.seconds ?: 0L

            val categoriesRaw = get("userSelectedCategories") as? List<Map<String, String>>
            val categoriesJoined = categoriesRaw
                ?.mapNotNull { it["reason"] }
                ?.joinToString(", ")
                ?: ""

            EmotionEntity(
                date = date,
                userInput = userInput,
                userSelectedMood = userSelectedMood,
                predictedMood = predictedMood,
                userSelectedCategories = categoriesJoined,
                timestamp = timestamp
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}