package com.example.uasecom.presentation.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uasecom.data.model.CartItem
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice = _totalPrice.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadCart(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = repository.getCartItems(userId)
                _cartItems.value = items
                calculateTotal(items)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(userId: String, product: Product, quantity: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("CartDebug", "Mencoba input: User $userId, Product ${product.title}, Qty $quantity")

                repository.addToCart(userId, product, quantity)

                Log.d("CartDebug", "Berhasil input ke Firestore!")

                // Reload cart agar data sinkron
                loadCart(userId)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CartDebug", "Gagal input: ${e.message}") // Cek Logcat bagian Error untuk pesan ini
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.totalPrice }
    }
}