package com.example.tugastodolist.data.model

import com.google.firebase.firestore.PropertyName


data class Todo (
    val id: String,
    val title: String,

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)