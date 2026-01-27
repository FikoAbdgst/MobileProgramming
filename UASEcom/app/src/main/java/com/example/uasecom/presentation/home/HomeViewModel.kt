package com.example.uasecom.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.ProductRepository
import com.example.uasecom.data.repository.WishlistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val wishlistProductIds: Set<Int> = emptySet(),
    val searchQuery: String = "" // Tambahkan Search Query
)

class HomeViewModel : ViewModel() {
    private val api = ProductApi.create()
    private val productRepository = ProductRepository(api)
    private val wishlistRepository = WishlistRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Backup data mentah untuk keperluan filter/search
    private var allProducts: List<Product> = emptyList()

    var selectedCategory by mutableStateOf<String?>(null)
        private set

    fun loadData(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val categoriesDeferred = async { productRepository.getCategories() }
                val productsDeferred = async { productRepository.getProducts() }
                val wishlistDeferred = async { wishlistRepository.getWishlistedProductIds(userId) }

                val categories = categoriesDeferred.await()
                val products = productsDeferred.await()
                val wishlistIds = wishlistDeferred.await()

                // Simpan ke backup
                allProducts = products

                _uiState.value = HomeUiState(
                    products = products,
                    categories = listOf("All") + categories,
                    wishlistProductIds = wishlistIds,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "An unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    // --- FITUR SEARCH DIKEMBALIKAN ---
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectCategory(category: String?) {
        selectedCategory = if (category == "All") null else category
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery
        val category = selectedCategory

        var filteredList = allProducts

        // 1. Filter Category
        if (category != null) {
            filteredList = filteredList.filter { it.category == category }
        }

        // 2. Filter Search
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(products = filteredList)
    }

    fun toggleWishlist(userId: String, productId: Int) {
        viewModelScope.launch {
            val currentWishlist = _uiState.value.wishlistProductIds
            val isCurrentlyWishlisted = currentWishlist.contains(productId)

            try {
                if (isCurrentlyWishlisted) {
                    wishlistRepository.removeFromWishlist(userId, productId)
                    _uiState.value = _uiState.value.copy(
                        wishlistProductIds = currentWishlist - productId
                    )
                } else {
                    wishlistRepository.addToWishlist(userId, productId)
                    _uiState.value = _uiState.value.copy(
                        wishlistProductIds = currentWishlist + productId
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}