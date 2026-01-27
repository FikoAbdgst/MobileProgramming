package com.example.uasecom.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WishlistRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getWishlistCollection(userId: String) =
        firestore.collection("users").document(userId).collection("wishlist")

    // Menambahkan produk ke wishlist (menyimpan ID produk sebagai dokumen)
    suspend fun addToWishlist(userId: String, productId: Int) {
        val data = mapOf("addedAt" to com.google.firebase.Timestamp.now())
        getWishlistCollection(userId).document(productId.toString()).set(data).await()
    }

    // Menghapus dari wishlist
    suspend fun removeFromWishlist(userId: String, productId: Int) {
        getWishlistCollection(userId).document(productId.toString()).delete().await()
    }

    // Mendapatkan semua ID produk yang ada di wishlist user saat ini
    suspend fun getWishlistedProductIds(userId: String): Set<Int> {
        val snapshot = getWishlistCollection(userId).get().await()
        // Mengembalikan kumpulan ID produk (Set agar pencarian cepat)
        return snapshot.documents.mapNotNull { it.id.toIntOrNull() }.toSet()
    }
}