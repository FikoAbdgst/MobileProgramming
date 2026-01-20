package com.example.quistodolist.data.repository

import com.example.quistodolist.data.model.Todo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class TodoRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Fungsi Add (Wajib pakai 'flow' agar bisa di-collect di ViewModel)
    fun addTodo(userId: String, title: String, priority: String, category: String) = flow {
        try {
            val todoId = firestore.collection("users").document(userId).collection("todos").document().id
            val todo = Todo(
                id = todoId,
                title = title,
                priority = priority,
                category = category, // Pastikan field ini ada
                isCompleted = false
            )
            firestore.collection("users").document(userId).collection("todos")
                .document(todoId)
                .set(todo)
                .await() // Wajib import kotlinx.coroutines.tasks.await
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    // Fungsi Get Todos (Realtime updates)
    fun getTodos(userId: String): Flow<List<Todo>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("todos")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val todos = snapshot?.documents?.mapNotNull { it.toObject(Todo::class.java) } ?: emptyList()
                trySend(todos)
            }
        awaitClose { listener.remove() }
    }

    // Fungsi Update (Edit data)
    fun updateTodo(userId: String, todoId: String, newTitle: String, newPriority: String, newCategory: String) = flow {
        try {
            val updates = mapOf(
                "title" to newTitle,
                "priority" to newPriority,
                "category" to newCategory
            )
            firestore.collection("users").document(userId).collection("todos")
                .document(todoId)
                .update(updates)
                .await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    // Fungsi Update Status (Centang/Uncentang)
    fun updateTodoStatus(userId: String, todoId: String, isCompleted: Boolean) = flow {
        try {
            firestore.collection("users").document(userId).collection("todos")
                .document(todoId)
                .update("isCompleted", isCompleted)
                .await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    // Fungsi Delete
    fun deleteTodo(userId: String, todoId: String) = flow {
        try {
            firestore.collection("users").document(userId).collection("todos")
                .document(todoId)
                .delete()
                .await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }
}