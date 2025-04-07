package com.sdapps.auraascend.view.home

import com.sdapps.auraascend.DayQuotes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface QuoteAPI {
    @GET("api/random")
    suspend fun getQuote(): DayQuotes

    @GET("api/quotes")
    suspend fun getAllQuotes(): ArrayList<DayQuotes>
}

object RetrofitBuilder {
    private const val BASE_URL = "https://qapi.vercel.app/"

    val api: QuoteAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuoteAPI::class.java)
}