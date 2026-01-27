package com.example.uasecom.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uasecom.data.ProductApi
import com.example.uasecom.data.UserData
import com.example.uasecom.data.model.Product
import com.example.uasecom.data.repository.ProductRepository

@Composable
fun HomeScreen(
    userData: UserData?,
    onProductClick: (Product) -> Unit
) {
    val api = remember { ProductApi.create() }
    val repository = remember { ProductRepository(api) }
    val viewModel = remember { HomeViewModel(repository) }
    val state by viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar & Filter (sama seperti sebelumnya)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(query = state.searchQuery, onQueryChange = viewModel::onSearchQueryChanged)
        Spacer(modifier = Modifier.height(16.dp))
        CategoryList(state.categories, state.selectedCategory, viewModel::onCategorySelected)
        Spacer(modifier = Modifier.height(16.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp) // Padding bawah agar tidak ketutup navbar
        ) {
            items(state.products) { product ->
                // Bungkus ProductItem dengan clickable
                Box(modifier = Modifier.clickable { onProductClick(product) }) {
                    ProductItem(product)
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(userData: UserData?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Welcome Back,", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = userData?.username ?: "User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text("Search clothes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF0F0F0),
            unfocusedContainerColor = Color(0xFFF0F0F0),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun CategoryList(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category.replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${product.price}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}