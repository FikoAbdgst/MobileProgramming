package com.example.uasecom.data.repository

import com.example.uasecom.data.model.CartItem
import com.example.uasecom.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Referensi ke collection cart user tertentu
    private fun getCartCollection(userId: String) =
        firestore.collection("users").document(userId).collection("cart")

    suspend fun addToCart(userId: String, product: Product, quantity: Int) {
        val cartRef = getCartCollection(userId).document(product.id.toString())

        // Cek apakah produk sudah ada di cart
        val snapshot = cartRef.get().await()

        if (snapshot.exists()) {
            // Update quantity
            val currentQty = snapshot.getLong("quantity")?.toInt() ?: 0
            val newQty = currentQty + quantity
            val newTotal = newQty * product.price

            cartRef.update(mapOf(
                "quantity" to newQty,
                "totalPrice" to newTotal
            )).await()
        } else {
            // Buat baru
            val newItem = CartItem(
                productId = product.id,
                productName = product.title,
                productPrice = product.price,
                productImage = product.image,
                quantity = quantity,
                totalPrice = product.price * quantity
            )
            cartRef.set(newItem).await()
        }
    }

    suspend fun getCartItems(userId: String): List<CartItem> {
        val snapshot = getCartCollection(userId).get().await()
        return snapshot.toObjects(CartItem::class.java)
    }
}