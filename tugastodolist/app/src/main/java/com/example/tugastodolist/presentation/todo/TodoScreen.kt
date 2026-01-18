package com.example.tugastodolist.presentation.todo

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tugastodolist.data.model.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoList") },
                actions = {
                    userData?.let {
                        Text(it.username ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(8.dp))
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = null,
                            modifier =  Modifier.size(35.dp).clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                                Text("Out", style =MaterialTheme.typography.labelSmall)
                            }
                    }
                }
            )
        }
    ){
        padding ->
        Column (Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically){
                TextField(
                    value = todoText,
                    onValueChange = {todoText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tambah tugas baru...") }
                )
                Button (onClick = {
                     if (todoText.isNotBlank()) {
                        userData?.userId?.let { viewModel.add(it, todoText) }
                        todoText
                    }
                 }, modifier = Modifier.padding(start = 8.dp)) {
                Text("Tambah")
            }
        }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                 items(todos) { todo ->
                     ListItem(
                         modifier = Modifier.clickable{onNavigateToEdit(todo.id)},
                         headlineContent = { Text(todo.title)},
                         leadingContent = {
                             Checkbox(
                                 checked = todo.isCompleted,
                                 onCheckedChange = { _ ->
                                     userData?.userId?.let { uid -> viewModel.toggle(uid, todo) }
                                 }
                             )
                         },
                         trailingContent = {
                             IconButton(onClick = {
                                 userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) }
                             }) {
                                 Icon(Icons.Default.Delete, contentDescription = null)
                             }
                         }
                     )
                 }
                 }
            }
        }
}