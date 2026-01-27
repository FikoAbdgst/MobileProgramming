package com.example.uasecom.presentation.wishtlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.UserData
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.ProductRepository
import com.example.uasecom.data.repository.WishlistRepository
import com.example.uasecom.presentation.home.ProductItemRedesigned
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Wishlist ViewModel
class WishlistViewModel : ViewModel() {
    private val api = ProductApi.create()
    private val productRepository = ProductRepository(api)
    private val wishlistRepository = WishlistRepository()

    private val _wishlistItems = MutableStateFlow<List<Product>>(emptyList())
    val wishlistItems = _wishlistItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadWishlist(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil ID dari Firestore
                val wishlistIds = wishlistRepository.getWishlistedProductIds(userId)

                if (wishlistIds.isEmpty()) {
                    _wishlistItems.value = emptyList()
                } else {
                    // Ambil semua produk dari API (karena FakeStoreAPI tidak support bulk get by IDs)
                    // Lalu filter di sisi aplikasi
                    val allProducts = productRepository.getProducts()
                    val filteredProducts = allProducts.filter { wishlistIds.contains(it.id) }
                    _wishlistItems.value = filteredProducts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromWishlist(userId: String, product: Product) {
        viewModelScope.launch {
            try {
                wishlistRepository.removeFromWishlist(userId, product.id)
                // Reload data setelah menghapus
                loadWishlist(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// 2. Wishlist Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    userData: UserData?,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    viewModel: WishlistViewModel = viewModel()
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.loadWishlist(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Wishlist", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (wishlistItems.isEmpty()) {
                Text(
                    "Your wishlist is empty",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(wishlistItems) { product ->
                        // Reuse komponen item produk dari HomeScreen
                        ProductItemRedesigned(
                            product = product,
                            isWishlisted = true, // Selalu true karena ini halaman wishlist
                            onClick = { onProductClick(product) },
                            onWishlistToggle = {
                                userData?.userId?.let { uid ->
                                    viewModel.removeFromWishlist(uid, product)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}