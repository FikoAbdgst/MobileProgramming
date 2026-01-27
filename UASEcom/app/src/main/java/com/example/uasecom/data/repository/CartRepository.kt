package com.example.uasecom.data.repository

import com.example.uasecom.data.model.CartItem
import com.example.uasecom.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getCartCollection(userId: String) =
        firestore.collection("carts").document(userId).collection("items")

    suspend fun addToCart(userId: String, product: Product, quantity: Int) {
        val cartRef = getCartCollection(userId).document(product.id.toString())
        val snapshot = cartRef.get().await()

        if (snapshot.exists()) {
            val currentQty = snapshot.getLong("quantity")?.toInt() ?: 0
            val newQty = currentQty + quantity
            updateCartItemQuantity(userId, product.id, newQty, product.price)
        } else {
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

    suspend fun updateCartItemQuantity(userId: String, productId: Int, newQuantity: Int, price: Double) {
        getCartCollection(userId).document(productId.toString())
            .update(mapOf(
                "quantity" to newQuantity,
                "totalPrice" to newQuantity * price
            )).await()
    }

    suspend fun deleteCartItem(userId: String, productId: Int) {
        getCartCollection(userId).document(productId.toString())
            .delete()
            .await()
    }
}