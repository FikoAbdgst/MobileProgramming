package com.example.quistodolist.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quistodolist.data.model.Todo
import com.example.quistodolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { _todos.value = it }
        }
    }

    fun add(userId: String, title: String, priority: String) = viewModelScope.launch {
        repository.addTodo(userId, title, priority)
    }
    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted)
    }

    fun updateTodo(userId: String, todoId: String, newTitle: String, newPriority: String) = viewModelScope.launch {
        repository.updateTodo(userId, todoId, newTitle, newPriority)
    }
    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId,todoId)
    }
}
