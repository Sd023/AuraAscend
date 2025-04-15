package com.sdapps.auraascend.view.home.fragments.funactivity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.core.NetworkTools
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.databinding.FragmentFunActivityBinding
import com.sdapps.auraascend.view.home.fragments.OnBackPress
import com.sdapps.auraascend.view.home.fragments.PodcastHelper.openPodcastsList
import com.sdapps.auraascend.view.home.fragments.funactivity.mindfulness.MindfulnessActivity
import com.sdapps.auraascend.view.home.fragments.funactivity.paddleplay.PaddlePlay
import com.sdapps.auraascend.view.home.fragments.funactivity.spotify.SpotifyActivity
import com.sdapps.auraascend.view.home.fragments.funactivity.storytime.StoryTimeActivity
import com.sdapps.auraascend.view.home.fragments.funactivity.swipeaquote.SwipeQuoteActivity
import com.sdapps.auraascend.view.home.spotify.FetchSong.fetchPodcasts
import com.sdapps.auraascend.view.home.spotify.PodcastShow
import com.sdapps.auraascend.view.home.spotify.getSpotifyToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


data class FunActivities (
    val activityIcon: Drawable?,
    val activityCode: String,
    val activityName: String,
    val activityTagLine: String
)
class FunActivityFragment : Fragment() {
    companion object {
        const val SWIPEAQUOTE = "SAQ"
        const val MINDFULNESS = "BREATHE"
        const val PADDLEGAME = "GAME"
        const val STORYTIME = "story"
        const val COMING_SOON = "no_idea"
        const val SPOTIFY = "spotify"
    }

    private var _binding: FragmentFunActivityBinding? = null
    private val binding get() = _binding!!

    private lateinit var spRef : SharedPrefHelper
    private val viewModel : DataViewModel by activityViewModels()
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DHANUSH", "onCreate")
        spRef = SharedPrefHelper(requireContext())
        progressDialog = CustomProgressDialog(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("DHANUSH", "onCreatedView")
        _binding = FragmentFunActivityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DHANUSH", "onViewCreated")
        init()
    }
    private fun init(){
        val mCtx = requireContext()
        val gameSharedPreference = mCtx.getSharedPreferences(SharedPrefHelper.SP_NAME, Context.MODE_PRIVATE)
        val gameHighScore = gameSharedPreference.getInt("HIGH_SCORE",0)


        val activityList = arrayListOf<FunActivities>(
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_swipe_quote_activity),SWIPEAQUOTE,"Swipe-a-Quote", "One swipe away from a better day.✨"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_mindful_activity),MINDFULNESS,"Mindfulness", "Breathe with me 🧘"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_story_activity),STORYTIME,"Story Time", "Let me tell a kutty story!"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_funactivity_games),PADDLEGAME, "Ping Pong", "Focus ON, High score UP! 🎯 \n HIGH SCORE : $gameHighScore"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_spotify),SPOTIFY,"Podcasts", "Listen to the curated podcasts!"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_sooon),COMING_SOON,"Under Construction", "More Activities are on the way!"),
            FunActivities(ContextCompat.getDrawable(mCtx,R.drawable.ic_sooon),COMING_SOON,"Under Construction", "More Activities are on the way!")
        )

        val mAdapter = FunActivityAdapter(activityList) { moduleCode ->
            onActivityCalled(moduleCode)
        }

        binding.activityRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.activityRV.adapter = mAdapter
    }

    private fun onActivityCalled(moduleCode: String){
        addCount()
        when(moduleCode){
            SWIPEAQUOTE -> {
              startActivity(Intent(requireActivity(), SwipeQuoteActivity::class.java))
            }

            MINDFULNESS -> {
                startActivity(Intent(requireActivity(), MindfulnessActivity::class.java))
            }

            STORYTIME -> {
                startActivity(Intent(requireActivity(), StoryTimeActivity::class.java))
            }

            PADDLEGAME -> {
                startActivity(Intent(requireActivity(), PaddlePlay::class.java))
            }

            SPOTIFY -> {
                openPodcastsList(requireContext(),progressDialog,viewModel,viewLifecycleOwner,spRef,null)
            }
        }
    }

    private fun addCount(){
        var count =1
        val pastAch = spRef.getAllActivites()
        if(pastAch > 0){
            count += pastAch
        }
        spRef.setAllActivity(count)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onStart() {
        super.onStart()
        Log.d("DHANUSH", "onStart")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("DHANUSH", "onAttach")
    }
}