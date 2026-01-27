package com.example.uasecom.presentation.wishtlist

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.UserData
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.CartRepository
import com.example.uasecom.data.repository.ProductRepository
import com.example.uasecom.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                if (wishlistIds.isEmpty()) {
                    _wishlistItems.value = emptyList()
                } else {
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

    fun removeFromWishlist(userId: String, productId: Int) {
        viewModelScope.launch {
            try {
                wishlistRepository.removeFromWishlist(userId, productId)
                _wishlistItems.value = _wishlistItems.value.filter { it.id != productId }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    userData: UserData?,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    viewModel: WishlistViewModel = viewModel()
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.loadWishlist(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wishlist", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
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
                    items(wishlistItems, key = { it.id }) { product ->
                        SwipeToRevealItem(
                            onDelete = {
                                userData?.userId?.let { uid ->
                                    viewModel.removeFromWishlist(uid, product.id)
                                }
                            },
                            content = {
                                WishlistItemContent(
                                    product = product,
                                    onClick = { onProductClick(product) },
                                    onAddToCart = {
                                        userData?.userId?.let { uid ->
                                            viewModel.addToCart(uid, product)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Added to Cart")
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemContent(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("$${product.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = onAddToCart,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Add to Cart", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun SwipeToRevealItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val deleteButtonSize = 80.dp
    val deleteButtonSizePx = with(density) { deleteButtonSize.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Tombol Trash (Layer Belakang)
        Box(
            modifier = Modifier
                .width(deleteButtonSize)
                .fillMaxHeight()
                .padding(start = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            FilledIconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Red),
                modifier = Modifier.size(56.dp), // Ukuran tombol diperbesar sedikit
                shape = RoundedCornerShape(12.dp) // KOTAK ROUNDED DI SINI
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }

        // Konten Utama (Layer Depan)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            val target = (offsetX.value + delta).coerceIn(-deleteButtonSizePx, 0f)
                            offsetX.snapTo(target)
                        }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (offsetX.value < -deleteButtonSizePx / 2) {
                            offsetX.animateTo(-deleteButtonSizePx)
                        } else {
                            offsetX.animateTo(0f)
                        }
                    }
                )
        ) {
            content()
        }
    }
}