package com.example.tugastodolist.presentation.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var selectedPriority by remember { mutableStateOf("Medium") }
    var showPriorityMenu by remember { mutableStateOf(false) } // State untuk menu dropdown

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
                            modifier = Modifier.size(35.dp).clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                            Text("Out", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // BAGIAN INPUT DIUBAH MENJADI SINGLE LINE (ROW)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Input Text (Mengisi sisa ruang)
                TextField(
                    value = todoText,
                    onValueChange = { todoText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tugas baru...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // 2. Pilihan Priority (Dropdown Hemat Tempat)
                Box {
                    TextButton(onClick = { showPriorityMenu = true }) {
                        Text(
                            text = selectedPriority,
                            color = getPriorityColor(selectedPriority),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    DropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        listOf("High", "Medium", "Low").forEach { priority ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = priority,
                                        color = getPriorityColor(priority)
                                    )
                                },
                                onClick = {
                                    selectedPriority = priority
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }
                }

                // 3. Tombol Tambah (Icon Button)
                IconButton(
                    onClick = {
                        if (todoText.isNotBlank()) {
                            userData?.userId?.let {
                                viewModel.add(it, todoText, selectedPriority)
                            }
                            todoText = ""
                            selectedPriority = "Medium"
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar Tugas
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(todos) { todo ->
                    ListItem(
                        modifier = Modifier
                            .clickable { onNavigateToEdit(todo.id) },
                        headlineContent = {
                            Text(
                                text = todo.title,
                                style = if (todo.isCompleted)
                                    MaterialTheme.typography.bodyLarge.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                else MaterialTheme.typography.bodyLarge
                            )
                        },
                        overlineContent = {
                            Text(
                                text = todo.priority,
                                color = getPriorityColor(todo.priority),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
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
                    HorizontalDivider()
                }
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "High" -> Color.Red
        "Medium" -> Color(0xFFFFA500) // Orange
        "Low" -> Color.Green
        else -> Color.Gray
    }
}