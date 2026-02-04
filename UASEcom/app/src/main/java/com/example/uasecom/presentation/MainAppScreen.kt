package com.example.uasecom.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uasecom.data.UserData
import com.example.uasecom.data.model.Product
import com.example.uasecom.presentation.cart.CartScreen
import com.example.uasecom.presentation.cart.CartViewModel
import com.example.uasecom.presentation.home.HomeScreen
import com.example.uasecom.presentation.home.HomeViewModel
import com.example.uasecom.presentation.home.ProductDetailSheet
import com.example.uasecom.presentation.profile.ProfileScreen
import com.example.uasecom.presentation.wishlist.WishlistScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    cartViewModel: CartViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current // Tambahkan ini untuk Intent WA
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(userData) {
        if (userData != null) {
            cartViewModel.loadCart(userData.userId)
        }
    }

    val homeUiState by homeViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCartItems = cartItems.size
    val cartTotal by cartViewModel.totalPrice.collectAsState()

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val isBottomBarVisible = currentRoute != "cart" && !showDetailSheet

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible || currentRoute == "cart",
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    if (currentRoute == "cart") {
                        CartBottomBar(
                            total = cartTotal,
                            onOrderClick = {
                                // Logika WhatsApp
                                val phoneNumber = "62882001330851"
                                val itemDetails = cartItems.joinToString(separator = "\n") { "- ${it.productName} (${it.quantity}x)" }
                                val message = "Halo admin, saya ingin order:\n\n$itemDetails\n\nTotal: $${String.format("%.2f", cartTotal)}"
                                val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
                                val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                            }
                        )
                    } else if (isBottomBarVisible) {
                        FloatingBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        userData = userData,
                        onProductClick = { product ->
                            selectedProduct = product
                            showDetailSheet = true
                        },
                        onCartClick = { navController.navigate("cart") },
                        onProfileClick = { navController.navigate("profile") },
                        viewModel = homeViewModel,
                        cartItemCount = totalCartItems // Parameter ini harus ada di HomeScreen.kt
                    )
                }
                composable("profile") {
                    ProfileScreen(userData = userData, onSignOut = onSignOut)
                }
                composable("cart") {
                    CartScreen(
                        userData = userData,
                        onBackClick = { navController.popBackStack() },
                        viewModel = cartViewModel
                    )
                }
                composable("wishlist") {
                    WishlistScreen(
                        userData = userData,
                        onBackClick = {
                            navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        },
                        onProductClick = { product ->
                            selectedProduct = product
                            showDetailSheet = true
                        },
                        onCartClick = { navController.navigate("cart") },
                        cartItemCount = totalCartItems // Parameter ini harus ada di WishlistScreen.kt
                    )
                }
            }

            // Logic Modal Sheet (Tetap sama)
            if (showDetailSheet && selectedProduct != null) {
                ModalBottomSheet(
                    onDismissRequest = { showDetailSheet = false },
                    sheetState = sheetState
                ) {
                    // ... isi modal tetap sama ...
                    val isWishlisted = homeUiState.wishlistProductIds.contains(selectedProduct!!.id)
                    ProductDetailSheet(
                        product = selectedProduct!!,
                        isWishlisted = isWishlisted,
                        onWishlistToggle = {
                            if (userData != null) homeViewModel.toggleWishlist(userData.userId, selectedProduct!!.id)
                        },
                        onAddToCart = { quantity ->
                            if (userData != null) {
                                cartViewModel.addToCart(userData.userId, selectedProduct!!, quantity) {
                                    scope.launch { sheetState.hide(); showDetailSheet = false }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun FloatingBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)) // Kurangi shadow sedikit
            .clip(RoundedCornerShape(20.dp))
            .height(70.dp), // Set tinggi manual jika perlu agar lebih ramping
        containerColor = Color.White,
        tonalElevation = 3.dp
    ) {
        val items = listOf(
            Triple("home", Icons.Filled.Home, Icons.Outlined.Home),
            Triple("wishlist", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
            Triple("profile", Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { (route, selectedIcon, unselectedIcon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                icon = {
                    Icon(
                        if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = route,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                label = { },
                selected = isSelected,
                onClick = { onNavigate(route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), // Warna indikator lebih soft
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}
@Composable
fun CartBottomBar(
    total: Double,
    onOrderClick: () -> Unit
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Price", fontSize = 14.sp, color = Color.Gray)
                Text(
                    "$${String.format("%.2f", total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = onOrderClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Order Now")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartIconWithBadge(
    itemCount: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge(
                        containerColor = Color.Red, // Warna merah agar mencolok
                        contentColor = Color.White
                    ) {
                        Text(
                            text = if (itemCount > 99) "99+" else itemCount.toString(),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart, // Ganti dengan icon cart Anda
                contentDescription = "Cart"
            )
        }
    }
}