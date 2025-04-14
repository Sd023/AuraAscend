package com.sdapps.auraascend.view.home.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.core.NetworkTools
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.view.home.fragments.funactivity.spotify.SpotifyActivity
import com.sdapps.auraascend.view.home.fragments.myday.UserEntryDialogFragment
import com.sdapps.auraascend.view.home.spotify.PodcastShow
import com.sdapps.auraascend.view.home.spotify.getSpotifyToken

object PodcastHelper {

    fun openPodcastsList(context: Context,
                         progressDialog: CustomProgressDialog,
                         viewModel: DataViewModel,
                         viewLifecycleOwner: LifecycleOwner,
                         spRef: SharedPrefHelper,
                         uEntry: UserEntryDialogFragment? = null){

        if(NetworkTools.isNetworkConnected(context)){
            progressDialog.showLoadingProgress("Fetching podcasts...")

            var isLaunched = false // to prevent multiple openings of screen due to live data observe
            viewModel.podcastList.observe(viewLifecycleOwner) { podcastLists ->
                if(!isLaunched && podcastLists != null){
                    uEntry?.dismiss()
                    isLaunched = true
                    progressDialog.closePialog()
                    val podList : ArrayList<PodcastShow> = ArrayList(podcastLists)
                    val bundle = Bundle().apply { putParcelableArrayList("pod_list",podList) }
                    val intent = Intent(context, SpotifyActivity::class.java).apply { putExtras(bundle) }
                    context.startActivity(intent)
                }
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            getSpotifyToken { token ->
                if (token != null) {
                    val predictedMood: String? = if(spRef.getPredictedMoodFromSharedPref()?.isNotEmpty() == true)
                        spRef.getPredictedMoodFromSharedPref()
                    else "joy"
                    predictedMood?.let { viewModel.getPodcasts(getMotivationalQuery(it), token) }
                } else {
                    Toast.makeText(context, "Failed to get token", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(context,"Connect to internet!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMotivationalQuery(label: String): String {
        val lowMoodLabels = setOf("sadness", "anger", "fear")
        return if (label.trim().lowercase() in lowMoodLabels) {
            "optimal living daily - personal development and self improvement"
        } else {
            "the school of greatness TED talks daily"
        }
    }
}