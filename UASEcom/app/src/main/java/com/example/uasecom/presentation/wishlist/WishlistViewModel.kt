package com.example.uasecom.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.CartRepository
import com.example.uasecom.data.repository.ProductRepository
import com.example.uasecom.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    private val api = ProductApi.create()
    private val productRepository = ProductRepository(api)
    private val wishlistRepository = WishlistRepository()
    private val cartRepository = CartRepository()

    private val _wishlistItems = MutableStateFlow<List<Product>>(emptyList())
    val wishlistItems = _wishlistItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadWishlist(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val wishlistIds = wishlistRepository.getWishlistedProductIds(userId)
                val products = if (wishlistIds.isEmpty()) {
                    emptyList()
                } else {
                    productRepository.getProducts()
                        .filter { wishlistIds.contains(it.id) }
                }
                _wishlistItems.value = products
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromWishlist(userId: String, productId: Int) {
        viewModelScope.launch {
            try {
                wishlistRepository.removeFromWishlist(userId, productId)
                _wishlistItems.value =
                    _wishlistItems.value.filterNot { it.id == productId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToCart(userId: String, product: Product) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(userId, product, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
