package com.example.quistodolist.presentation.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quistodolist.data.model.UserData

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
                    Column {
                        Text(
                            "Noteapp",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A73E8)
                        )
                        userData?.username?.let {
                            Text(
                                "Hello, $it",
                                fontSize = 12.sp,
                                color = Color(0xFF5F6368),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    userData?.let {
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onSignOut() }
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "My Tasks",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
                Text(
                    text = "${todos.count { !it.isCompleted }} tasks pending",
                    fontSize = 14.sp,
                    color = Color(0xFF5F6368),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = todoText,
                            onValueChange = { todoText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Add a new task...",
                                    color = Color(0xFF9AA0A6)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1A73E8),
                                unfocusedBorderColor = Color(0xFFDADCE0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Priority Selector
                            Box {
                                OutlinedButton(
                                    onClick = { showPriorityMenu = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = getPriorityBackgroundColor(selectedPriority)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 0.dp
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                getPriorityColor(selectedPriority),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        selectedPriority,
                                        color = getPriorityColor(selectedPriority),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                                DropdownMenu(
                                    expanded = showPriorityMenu,
                                    onDismissRequest = { showPriorityMenu = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    listOf("High", "Medium", "Low").forEach { priority ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .background(
                                                                getPriorityColor(priority),
                                                                CircleShape
                                                            )
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        priority,
                                                        color = Color(0xFF202124),
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedPriority = priority
                                                showPriorityMenu = false
                                            },
                                            colors = MenuDefaults.itemColors(
                                                textColor = Color(0xFF202124)
                                            )
                                        )
                                    }
                                }
                            }

                            // Add Button
                            Button(
                                onClick = {
                                    if (todoText.isNotBlank()) {
                                        userData?.userId?.let {
                                            viewModel.add(it, todoText, selectedPriority)
                                        }
                                        todoText = ""
                                        selectedPriority = "Medium"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1A73E8)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Task", fontWeight = FontWeight.Medium, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tasks List
                if (todos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tasks yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5F6368)
                        )
                        Text(
                            text = "Add your first task to get started",
                            fontSize = 14.sp,
                            color = Color(0xFF9AA0A6),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todostodos, key = { it.id }) { todo ->
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
    }
}

@Composable
fun TodoItemCard(
    todo: com.example.quistodolist.data.model.Todo,
    onItemClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Custom Checkbox
            IconButton(
                onClick = { onToggle(!todo.isCompleted) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (todo.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Toggle",
                    tint = if (todo.isCompleted) Color(0xFF1A73E8) else Color(0xFFDADCE0),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (todo.isCompleted) Color(0xFF9AA0A6) else Color(0xFF202124),
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                getPriorityColor(todo.priority),
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = todo.priority,
                        fontSize = 12.sp,
                        color = getPriorityColor(todo.priority),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFF9AA0A6),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "High" -> Color(0xFFEA4335)
        "Medium" -> Color(0xFFFBBC04)
        "Low" -> Color(0xFF34A853)
        else -> Color(0xFF9AA0A6)
    }
}

fun getPriorityBackgroundColor(priority: String): Color {
    return when (priority) {
        "High" -> Color(0xFFFEF0EF)
        "Medium" -> Color(0xFFFEF7E0)
        "Low" -> Color(0xFFE6F4EA)
        else -> Color(0xFFF1F3F4)
    }
}