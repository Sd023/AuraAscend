package com.sdapps.auraascend.view.home.spotify

import android.media.Image
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyPodcastService {
    @GET("v1/search")
    fun searchPodcasts(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String = "show",
        @Query("limit") limit: Int = 10
    ): Call<SpotifyPodcastResponse>
}


data class SpotifyPodcastResponse(@SerializedName("shows") val shows: Shows)
data class Shows(@SerializedName("items") val items: List<PodcastShow>)

@Parcelize
data class PodcastShow(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("images") val images: List<com.sdapps.auraascend.view.home.spotify.Image>,
    @SerializedName("external_urls") val externalUrls: ExternalUrls
) : Parcelable

@Parcelize
data class Image(@SerializedName("url") val url: String): Parcelable
@Parcelize
data class ExternalUrls(@SerializedName("spotify") val spotify: String): Parcelable
