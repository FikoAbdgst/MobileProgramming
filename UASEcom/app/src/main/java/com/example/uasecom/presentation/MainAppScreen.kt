package com.example.uasecom.presentation

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
import com.example.uasecom.presentation.home.ProductDetailSheet
import com.example.uasecom.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // State untuk Modal Detail Product
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Ambil total harga untuk Bottom Bar Cart
    val cartTotal by cartViewModel.totalPrice.collectAsState()

    // Logika Visibilitas Navbar
    // Sembunyikan navbar jika sedang di halaman Cart atau jika Modal Detail sedang terbuka
    val isBottomBarVisible = currentRoute != "cart" && !showDetailSheet

    Scaffold(
        // TOP BAR: Icon Cart hanya muncul di Home/Profile
        topBar = {
            if (currentRoute != "cart") {
                CenterAlignedTopAppBar(
                    title = { Text("E-Commerce", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                )
            }
        },

        // BOTTOM BAR (Kondisional)
        bottomBar = {
            if (currentRoute == "cart") {
                // KONDISI 2: Tampilan Bottom Bar khusus Cart (Total + Order Now)
                CartBottomBar(
                    total = cartTotal,
                    onOrderClick = { /* Logic WhatsApp Nanti */ }
                )
            } else if (isBottomBarVisible) {
                // KONDISI 1: Floating Navbar (Hanya muncul jika modal tutup & bukan di cart)
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
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // HomeScreen sekarang perlu menerima callback ketika produk diklik
                HomeScreen(
                    userData = userData,
                    onProductClick = { product ->
                        selectedProduct = product
                        showDetailSheet = true
                    }
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
        }

        if (showDetailSheet && selectedProduct != null) {
            ModalBottomSheet(
                onDismissRequest = { showDetailSheet = false },
                sheetState = sheetState
            ) {
                ProductDetailSheet(
                    product = selectedProduct!!,
                    onAddToCart = { quantity ->
                        if (userData != null) {
                            cartViewModel.addToCart(userData.userId, selectedProduct!!, quantity) {
                                scope.launch {
                                    sheetState.hide()
                                    showDetailSheet = false
                                }
                            }
                        } else {
                            // Tambahkan ini untuk cek apakah user terdeteksi login
                            println("Error: User Data is Null!")
                        }
                    }
                )
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
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        containerColor = Color.White,
        tonalElevation = 5.dp
    ) {
        // 1. Home
        NavigationBarItem(
            icon = { Icon(if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home, null) },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surfaceVariant)
        )

        // 2. Wishlist (TAMBAHKAN INI KEMBALI)
        NavigationBarItem(
            icon = { Icon(if (currentRoute == "wishlist") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null) },
            label = { Text("Wishlist") },
            selected = currentRoute == "wishlist",
            onClick = { onNavigate("wishlist") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surfaceVariant)
        )

        // 3. Profile
        NavigationBarItem(
            icon = { Icon(if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person, null) },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}
// Komponen Bottom Bar khusus Cart
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