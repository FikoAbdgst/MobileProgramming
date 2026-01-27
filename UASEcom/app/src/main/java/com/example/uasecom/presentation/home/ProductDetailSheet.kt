package com.example.uasecom.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uasecom.data.model.Product

@Composable
fun ProductDetailSheet(
    product: Product,
    onAddToCart: (Int) -> Unit // Mengirim quantity kembali ke screen utama
) {
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .heightIn(min = 400.dp) // Tinggi minimal modal
    ) {
        // Gambar Produk
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Judul & Harga
        Text(text = product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "$${product.price}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Deskripsi (Scrollable jika panjang)
        Text(
            text = product.description,
            fontSize = 14.sp,
            color = Color.Gray,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input Quantity
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Quantity", fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.background(Color.LightGray.copy(alpha=0.3f), shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }

                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button Add to Cart
        Button(
            onClick = { onAddToCart(quantity) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add to Cart - $${product.price * quantity}")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}