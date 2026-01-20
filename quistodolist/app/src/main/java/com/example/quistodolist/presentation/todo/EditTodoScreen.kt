package com.example.quistodolist.presentation.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quistodolist.data.model.Todo

// Color Palette (sama dengan TodoScreen)
private val PrimaryColor = Color(0xFF6366F1)
private val PrimaryLight = Color(0xFFE0E7FF)
private val SecondaryColor = Color(0xFF8B5CF6)
private val AccentColor = Color(0xFF06B6D4)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBackground = Color.White
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var priority by remember { mutableStateOf(todo.priority) }
    var category by remember { mutableStateOf(todo.category) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Tugas",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Judul Tugas
                    Text(
                        "Judul Tugas",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,

                            focusedContainerColor = BackgroundColor,
                            unfocusedContainerColor = BackgroundColor,

                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = BorderColor,

                            cursorColor = PrimaryColor
                        ),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Kategori
                    Text(
                        "Kategori",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryChip(
                            label = "Kerja",
                            isSelected = category == "Kerja",
                            onClick = { category = "Kerja" },
                            color = PrimaryColor,
                            lightColor = PrimaryLight
                        )
                        CategoryChip(
                            label = "Kuliah",
                            isSelected = category == "Kuliah",
                            onClick = { category = "Kuliah" },
                            color = SecondaryColor,
                            lightColor = Color(0xFFF3E8FF)
                        )
                        CategoryChip(
                            label = "Hobby",
                            isSelected = category == "Hobby",
                            onClick = { category = "Hobby" },
                            color = AccentColor,
                            lightColor = Color(0xFFCFFAFE)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Prioritas
                    Text(
                        "Prioritas",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PriorityButton(
                            label = "High",
                            color = Color(0xFFEF4444),
                            lightColor = Color(0xFFFEE2E2),
                            selectedPriority = priority
                        ) { priority = "High" }

                        PriorityButton(
                            label = "Medium",
                            color = Color(0xFFF59E0B),
                            lightColor = Color(0xFFFEF3C7),
                            selectedPriority = priority
                        ) { priority = "Medium" }

                        PriorityButton(
                            label = "Low",
                            color = Color(0xFF10B981),
                            lightColor = Color(0xFFD1FAE5),
                            selectedPriority = priority
                        ) { priority = "Low" }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onSave(title, priority, category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Simpan Perubahan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RowScope.CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color,
    lightColor: Color
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color else lightColor)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 0.dp else 1.5.dp,
                color = if (isSelected) Color.Transparent else BorderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun RowScope.PriorityButton(
    label: String,
    color: Color,
    lightColor: Color,
    selectedPriority: String,
    onClick: () -> Unit
) {
    val isSelected = selectedPriority == label

    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color else lightColor)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 0.dp else 1.5.dp,
                color = if (isSelected) Color.Transparent else BorderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isSelected) Color.White else color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}