package com.sdapps.auraascend.view.home.spotify

import android.util.Base64
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface SpotifyAuthService {
    @FormUrlEncoded
    @POST("api/token")
    fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Call<SpotifyAuthResponse>
}

data class SpotifyAuthResponse(
    @SerializedName("access_token") val accessToken: String
)
val CLIENT_ID = "d21eea5054024785968e281ab1ba5351"
val CLIENT_SECRET = "cd339ed69a5749c89d5703652133d273"

fun getSpotifyToken(callback: (String?) -> Unit) {
    val authHeader = "Basic " + Base64.encodeToString(
        "$CLIENT_ID:$CLIENT_SECRET".toByteArray(), Base64.NO_WRAP
    )

    val retrofit = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(SpotifyAuthService::class.java)

    service.getToken(authHeader).enqueue(object : Callback<SpotifyAuthResponse> {
        override fun onResponse(call: Call<SpotifyAuthResponse>, response: Response<SpotifyAuthResponse>) {
            if (response.isSuccessful) {
                callback(response.body()?.accessToken)
            } else {
                callback(null)
            }
        }

        override fun onFailure(call: Call<SpotifyAuthResponse>, t: Throwable) {
            callback(null)
        }
    })
}
