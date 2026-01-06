package com.example.sideeffek.services

import com.example.sideeffek.model.Quote
import retrofit2.http.GET

interface ApiServices {
    @GET("quotes/random")
    suspend fun getRandomQuote(): Quote
}