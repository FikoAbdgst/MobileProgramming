package com.example.sideeffek.repository

import com.example.sideeffek.model.Quote
import com.example.sideeffek.services.ApiServices
import io.github.cdimascio.dotenv.dotenv
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteRepository {

//    val dotEnv = dotenv {
//        directory = "/assets"
//        filename = "env"
//    }
//    val QuoteApiUrl: String = dotEnv["DUMMY_JSON_URL"]

    val quoteApiUrl = "https://dummyjson.com/"

    private val api: ApiServices by lazy {
        Retrofit.Builder()
            .baseUrl(quoteApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }

    suspend fun getRandomQuote(): Quote {
        return api.getRandomQuote()

    }
}