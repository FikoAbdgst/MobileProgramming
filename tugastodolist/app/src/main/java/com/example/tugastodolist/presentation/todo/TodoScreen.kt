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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    var showPriorityMenu by remember { mutableStateOf(false) }

    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Tasks", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    userData?.let {
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                            Text("Logout", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
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
            // --- INPUT AREA (CARD) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tugas baru...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    // Priority Chip
                    Box {
                        AssistChip(
                            onClick = { showPriorityMenu = true },
                            label = { Text(selectedPriority) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = getPriorityColor(selectedPriority)
                            )
                        )
                        DropdownMenu(
                            expanded = showPriorityMenu,
                            onDismissRequest = { showPriorityMenu = false }
                        ) {
                            listOf("High", "Medium", "Low").forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority, color = getPriorityColor(priority)) },
                                    onClick = {
                                        selectedPriority = priority
                                        showPriorityMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

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
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LIST TUGAS ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onItemClick = { onNavigateToEdit(todo.id) },
                        onToggle = {
                            userData?.userId?.let { uid -> viewModel.toggle(uid, todo) }
                        },
                        onDelete = {
                            userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: com.example.tugastodolist.data.model.Todo,
    onItemClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indikator Warna Prioritas
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(getPriorityColor(todo.priority))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Checkbox(checked = todo.isCompleted, onCheckedChange = onToggle)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = if (todo.isCompleted)
                        MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    else MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = todo.priority,
                    style = MaterialTheme.typography.labelSmall,
                    color = getPriorityColor(todo.priority)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "High" -> Color(0xFFE53935)
        "Medium" -> Color(0xFFFB8C00)
        "Low" -> Color(0xFF43A047)
        else -> Color.Gray
    }
}