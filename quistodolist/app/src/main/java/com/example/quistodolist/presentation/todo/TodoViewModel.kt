package com.example.quistodolist.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quistodolist.data.model.Todo
import com.example.quistodolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()

    // Data asli dari database
    private val _rawTodos = MutableStateFlow<List<Todo>>(emptyList())

    // State untuk Pencarian dan Filter
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow("Semua")

    // --- LOGIC STATISTIK (BARU) ---
    // Mengembalikan Triple: (Total Tugas, Tugas Selesai, Progress Float 0.0 - 1.0)
    val statistics = _rawTodos.map { list ->
        val total = list.size
        val completed = list.count { it.isCompleted }
        val progress = if (total > 0) completed.toFloat() / total else 0f
        Triple(total, completed, progress)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0f))

    // Logic Utama Filter & Search
    val todos = combine(_rawTodos, _searchQuery, _selectedFilter) { todos, query, filter ->
        todos.filter { todo ->
            val matchesQuery = todo.title.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "Semua" -> true
                "Belum Selesai" -> !todo.isCompleted
                else -> todo.category == filter
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchQuery = _searchQuery.asStateFlow()
    val selectedFilter = _selectedFilter.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect {
                _rawTodos.value = it
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChange(newFilter: String) {
        _selectedFilter.value = newFilter
    }

    // --- CRUD Operations ---
    fun add(userId: String, title: String, priority: String, category: String) = viewModelScope.launch {
        repository.addTodo(userId, title, priority, category).collect { }
    }

    fun updateTodo(userId: String, todoId: String, newTitle: String, newPriority: String, newCategory: String) = viewModelScope.launch {
        repository.updateTodo(userId, todoId, newTitle, newPriority, newCategory).collect { }
    }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted).collect { }
    }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId).collect { }
    }
}