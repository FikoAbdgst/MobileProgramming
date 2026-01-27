package com.example.uasecom.data.repository

import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.model.Product

class ProductRepository(private val api: ProductApi) {
    suspend fun getProducts(): List<Product> {
        return api.getAllProducts()
    }

    suspend fun getCategories(): List<String> {
        return api.getCategories()
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return api.getProductsByCategory(category)
    }
}