package com.sdapps.auraascend.view.home.spotify

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object FetchSong {
    fun fetchPodcasts(token: String, query: String, callback: (List<PodcastShow>?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SpotifyPodcastService::class.java)

        service.searchPodcasts("Bearer $token", query, "show", 10).enqueue(object : Callback<SpotifyPodcastResponse> {
            override fun onResponse(call: Call<SpotifyPodcastResponse>, response: Response<SpotifyPodcastResponse>) {
                if (response.isSuccessful) {
                    val podcasts = response.body()?.shows?.items
                    callback(podcasts)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<SpotifyPodcastResponse>, t: Throwable) {
                callback(null)
            }
        })
    }


}

