package com.example.pertemuan3fiko

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pertemuan3fiko.ui.theme.Pertemuan3FikoTheme

@Composable
fun KartuIdentitasMahasiswa(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F3460)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.foto_mahasiswa),
                    contentDescription = "Foto Mahasiswa",
                    modifier = Modifier
                        .width(100.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF7B2FF7)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "KARTU MAHASISWA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D9FF),
                    letterSpacing = 2.sp
                )

                Divider(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = Color(0xFF00D9FF).copy(alpha = 0.5f)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Text(
                            text = "Nama:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8B8D1),
                            fontSize = 14.sp,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "Fiko Abdigusti",
                            textAlign = TextAlign.Start,
                            color = Color(0xFFE8E8F0),
                            fontSize = 14.sp
                        )
                    }
                    Row {
                        Text(
                            text = "NIM:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8B8D1),
                            fontSize = 14.sp,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "23010044",
                            color = Color(0xFFE8E8F0),
                            fontSize = 14.sp
                        )
                    }
                    Row {
                        Text(
                            text = "Jurusan:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8B8D1),
                            fontSize = 14.sp,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "D3 TEKNIK INFORMATIKA",
                            color = Color(0xFFE8E8F0),
                            fontSize = 14.sp
                        )
                    }
                    Row {
                        Text(
                            text = "Universitas:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8B8D1),
                            fontSize = 14.sp,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "STMIK Mardira Indonesia",
                            color = Color(0xFFE8E8F0),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KartuIdentitasMahasiswaPreview() {
    Pertemuan3FikoTheme {
        KartuIdentitasMahasiswa()
    }
}