package com.example.uasecom.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ProductRepository) : ViewModel() {

    // State utama UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Data mentah (backup untuk search/filter)
    private var allProducts: List<Product> = emptyList()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Ambil data produk dan kategori secara bersamaan (async)
                val products = repository.getProducts()
                val categories = repository.getCategories()

                allProducts = products

                _uiState.value = _uiState.value.copy(
                    products = products,
                    categories = listOf("All") + categories, // Tambah opsi "All"
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery
        val category = _uiState.value.selectedCategory

        var filteredList = allProducts

        // Filter by Category
        if (category != "All") {
            filteredList = filteredList.filter { it.category == category }
        }

        // Filter by Search Query
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(products = filteredList)
    }
}

// Data class untuk menampung seluruh status layar Home
data class HomeUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "All"
)