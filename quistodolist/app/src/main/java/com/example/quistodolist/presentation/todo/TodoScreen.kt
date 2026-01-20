package com.example.quistodolist.presentation.todo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quistodolist.data.model.Todo
import com.example.quistodolist.data.model.UserData

// Color Palette
private val PrimaryColor = Color(0xFF6366F1) // Indigo
private val PrimaryLight = Color(0xFFE0E7FF)
private val SecondaryColor = Color(0xFF8B5CF6) // Purple
private val AccentColor = Color(0xFF06B6D4) // Cyan
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBackground = Color.White
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    // State Input
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var selectedCategory by remember { mutableStateOf("Kerja") }

    // State Menu
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Data ViewModel
    val todos by viewModel.todos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()
    val stats by viewModel.statistics.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (userData?.profilePictureUrl != null) {
                            AsyncImage(
                                model = userData.profilePictureUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column {
                            Text(
                                text = "Halo, ${userData?.username?.split(" ")?.firstOrNull() ?: "User"}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Semangat menyelesaikan tugasmu!",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBackground)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // === DASHBOARD STATISTIK ===
            DashboardCard(
                totalTasks = stats.first,
                completedTasks = stats.second,
                progress = stats.third
            )

            Spacer(modifier = Modifier.height(20.dp))

            // === SEARCH BAR ===
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Cari tugas...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },

                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,

                    focusedContainerColor = BackgroundColor,
                    unfocusedContainerColor = BackgroundColor,

                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderColor,

                    cursorColor = PrimaryColor
                ),
                singleLine = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === FILTER TABS ===
            val filters = listOf("Semua", "Belum Selesai", "Kerja", "Kuliah", "Hobby")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == currentFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onFilterChange(filter) },
                        label = {
                            Text(
                                filter,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryColor,
                            selectedLabelColor = Color.White,
                            containerColor = CardBackground,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) PrimaryColor else BorderColor,
                            enabled = true,
                            selected = isSelected,
                            borderWidth = 1.5.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === INPUT CARD ===
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tambah tugas baru...", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,

                            focusedContainerColor = BackgroundColor,
                            unfocusedContainerColor = BackgroundColor,

                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = BorderColor,

                            cursorColor = PrimaryColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (todoText.isNotBlank()) {
                                userData?.userId?.let { viewModel.add(it, todoText, selectedPriority, selectedCategory) }
                                todoText = ""
                                focusManager.clearFocus()
                            }
                        })
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Priority
                            Box {
                                OutlinedButton(
                                    onClick = { showPriorityMenu = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = getPriorityBackgroundColor(selectedPriority)
                                    ),
                                    border = null
                                ) {
                                    Box(Modifier.size(8.dp).background(getPriorityColor(selectedPriority), CircleShape))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        selectedPriority,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                DropdownMenu(
                                    expanded = showPriorityMenu,
                                    onDismissRequest = { showPriorityMenu = false },
                                    modifier = Modifier.background(CardBackground)
                                ) {
                                    listOf("High", "Medium", "Low").forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p) },
                                            onClick = {
                                                selectedPriority = p
                                                showPriorityMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                            // Category
                            Box {
                                OutlinedButton(
                                    onClick = { showCategoryMenu = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = PrimaryLight
                                    ),
                                    border = null
                                ) {
                                    Text(
                                        selectedCategory,
                                        fontSize = 13.sp,
                                        color = PrimaryColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                DropdownMenu(
                                    expanded = showCategoryMenu,
                                    onDismissRequest = { showCategoryMenu = false },
                                    modifier = Modifier.background(CardBackground)
                                ) {
                                    listOf("Kerja", "Kuliah", "Hobby", "Lainnya").forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c) },
                                            onClick = {
                                                selectedCategory = c
                                                showCategoryMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                if (todoText.isNotBlank()) {
                                    userData?.userId?.let { viewModel.add(it, todoText, selectedPriority, selectedCategory) }
                                    todoText = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Tambah", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === LIST TUGAS ===
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (todos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Tidak ada tugas",
                                    color = TextSecondary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Tambahkan tugas pertamamu!",
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(todos, key = { it.id }) { todo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                        Color(0xFFEF4444) else Color.Transparent,
                                    label = "bgColor"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color)
                                        .padding(end = 24.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            },
                            content = {
                                TodoItemCard(
                                    todo = todo,
                                    onItemClick = { onNavigateToEdit(todo.id) },
                                    onToggle = { userData?.userId?.let { uid -> viewModel.toggle(uid, todo) } },
                                    onDelete = { userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) } }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    totalTasks: Int,
    completedTasks: Int,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryColor, SecondaryColor)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Statistik Tugas",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% Selesai",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedTasks dari $totalTasks tugas",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(80.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        strokeWidth = 8.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(80.dp),
                        color = Color.White,
                        strokeWidth = 8.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: Todo,
    onItemClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onToggle(!todo.isCompleted) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = "Toggle",
                    tint = if (todo.isCompleted) PrimaryColor else BorderColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(getCategoryBackgroundColor(todo.category), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = todo.category,
                        fontSize = 11.sp,
                        color = getCategoryColor(todo.category),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = todo.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (todo.isCompleted) TextSecondary else TextPrimary,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(getPriorityColor(todo.priority), CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = todo.priority,
                        fontSize = 12.sp,
                        color = getPriorityColor(todo.priority),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun getPriorityColor(priority: String): Color = when (priority) {
    "High" -> Color(0xFFEF4444)
    "Medium" -> Color(0xFFF59E0B)
    "Low" -> Color(0xFF10B981)
    else -> TextSecondary
}

fun getPriorityBackgroundColor(priority: String): Color = when (priority) {
    "High" -> Color(0xFFFEE2E2)
    "Medium" -> Color(0xFFFEF3C7)
    "Low" -> Color(0xFFD1FAE5)
    else -> BackgroundColor
}

fun getCategoryColor(category: String): Color = when (category) {
    "Kerja" -> Color(0xFF6366F1)
    "Kuliah" -> Color(0xFF8B5CF6)
    "Hobby" -> Color(0xFF06B6D4)
    else -> TextSecondary
}

fun getCategoryBackgroundColor(category: String): Color = when (category) {
    "Kerja" -> Color(0xFFE0E7FF)
    "Kuliah" -> Color(0xFFF3E8FF)
    "Hobby" -> Color(0xFFCFFAFE)
    else -> BackgroundColor
}