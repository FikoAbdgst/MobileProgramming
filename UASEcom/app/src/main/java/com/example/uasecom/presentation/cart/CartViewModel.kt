package com.example.uasecom.presentation.cart

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
                repository.addToCart(userId, product, quantity)
                loadCart(userId)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun increaseQuantity(userId: String, item: CartItem) {
        viewModelScope.launch {
            try {
                val newQty = item.quantity + 1
                repository.updateCartItemQuantity(userId, item.productId, newQty, item.productPrice)
                loadCart(userId) // Reload UI
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun decreaseQuantity(userId: String, item: CartItem) {
        if (item.quantity <= 1) return // Cegah kurang dari 1

        viewModelScope.launch {
            try {
                val newQty = item.quantity - 1
                repository.updateCartItemQuantity(userId, item.productId, newQty, item.productPrice)
                loadCart(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteItem(userId: String, productId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteCartItem(userId, productId)
                loadCart(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.totalPrice }
    }
}