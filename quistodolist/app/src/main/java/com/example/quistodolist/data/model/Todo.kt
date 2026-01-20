package com.example.quistodolist.data.model

import com.google.firebase.firestore.PropertyName

data class Todo(
    val id: String = "",
    val title: String = "",

    val priority: String = "Medium",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)