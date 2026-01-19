package com.example.tugastodolist.presentation.todo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tugastodolist.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
){
    var title by remember { mutableStateOf(todo.title) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var showPriorityMenu by remember { mutableStateOf(false) } // State untuk menu dropdown

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(todo.createdAt))

    Scaffold(
        topBar = {
            TopAppBar(
                title =  { Text("Edit Tugas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Menggunakan ikon back standar
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // BAGIAN EDIT (SINGLE LINE: Judul + Priority + Save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Input Judul (Mengisi sisa ruang)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
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

                // 3. Tombol Simpan (Icon Button)
                IconButton(
                    onClick = { onSave(title, selectedPriority) },
                    enabled = title.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Info Tanggal di bawahnya
            Text(
                text = "Dibuat pada: $dateString",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}