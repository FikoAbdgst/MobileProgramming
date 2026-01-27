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
            // Loading bisa tetap ada untuk operasi add to cart dari luar
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

    // UPDATE REALTIME: Optimistic Update tanpa Loading Full Screen
    fun increaseQuantity(userId: String, item: CartItem) {
        viewModelScope.launch {
            // Update UI Lokal dulu (Optimistic)
            val currentList = _cartItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.productId == item.productId }
            if (index != -1) {
                val updatedItem = item.copy(quantity = item.quantity + 1, totalPrice = (item.quantity + 1) * item.productPrice)
                currentList[index] = updatedItem
                _cartItems.value = currentList
                calculateTotal(currentList)
            }

            // Lalu update ke Backend
            try {
                val newQty = item.quantity + 1
                repository.updateCartItemQuantity(userId, item.productId, newQty, item.productPrice)
                // Tidak perlu reload cart full jika sukses, karena lokal sudah update
            } catch (e: Exception) {
                e.printStackTrace()
                // Revert jika gagal (Opsional)
                loadCart(userId)
            }
        }
    }

    fun decreaseQuantity(userId: String, item: CartItem) {
        if (item.quantity <= 1) return

        viewModelScope.launch {
            // Update UI Lokal dulu (Optimistic)
            val currentList = _cartItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.productId == item.productId }
            if (index != -1) {
                val updatedItem = item.copy(quantity = item.quantity - 1, totalPrice = (item.quantity - 1) * item.productPrice)
                currentList[index] = updatedItem
                _cartItems.value = currentList
                calculateTotal(currentList)
            }

            try {
                val newQty = item.quantity - 1
                repository.updateCartItemQuantity(userId, item.productId, newQty, item.productPrice)
            } catch (e: Exception) {
                e.printStackTrace()
                loadCart(userId)
            }
        }
    }

    fun deleteItem(userId: String, productId: Int) {
        viewModelScope.launch {
            // Hapus lokal dulu
            val currentList = _cartItems.value.filter { it.productId != productId }
            _cartItems.value = currentList
            calculateTotal(currentList)

            try {
                repository.deleteCartItem(userId, productId)
            } catch (e: Exception) {
                e.printStackTrace()
                loadCart(userId) // Revert jika gagal
            }
        }
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.totalPrice }
    }
}