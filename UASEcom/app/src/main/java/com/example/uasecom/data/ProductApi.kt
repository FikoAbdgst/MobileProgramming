package com.example.uasecom.data

import com.example.uasecom.data.model.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductApi {
    @GET("products")
    suspend fun getAllProducts(): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    companion object {
        const val BASE_URL = "https://fakestoreapi.com/"

        fun create(): ProductApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductApi::class.java)
        }
    }
}