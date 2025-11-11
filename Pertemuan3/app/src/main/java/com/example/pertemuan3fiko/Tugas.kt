package com.example.pertemuan3fiko

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
fun KartuIdentitasMahasiswa(){ // Fungsi composable untuk menampilkan kartu identitas mahasiswa
    Box( // Kontainer utama untuk menampung elemen-elemen lain secara tumpuk
        modifier = Modifier
            .fillMaxWidth() // Lebar Box memenuhi layar
            .background( // Memberi background gradasi vertikal
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBF092F), // Warna atas
                        Color(0xFF16213E)  // Warna bawah
                    )
                )
            )
            .padding(20.dp), // Jarak tepi luar Box
        contentAlignment = Alignment.TopCenter // Posisi isi di tengah atas
    ) {
        Card( // Membuat kartu sebagai wadah utama data mahasiswa
            modifier = Modifier.fillMaxWidth(), // Kartu selebar layar
            shape = RoundedCornerShape(16.dp), // Sudut kartu melengkung 16dp
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Memberi efek bayangan
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F3460) // Warna latar kartu
            )
        ) {
            Column( // Menyusun elemen di dalam kartu secara vertikal
                horizontalAlignment = Alignment.CenterHorizontally, // Rata tengah horizontal
                modifier = Modifier.padding(24.dp) // Jarak isi dari tepi kartu
            ) {
                Image( // Menampilkan foto mahasiswa
                    painter = painterResource(id = R.drawable.foto_mahasiswa), // Gambar dari resource
                    contentDescription = "Foto Mahasiswa",
                    modifier = Modifier
                        .width(100.dp) // Lebar gambar 100dp
                        .height(130.dp) // Tinggi gambar 130dp
                        .clip(RoundedCornerShape(12.dp)) // Gambar dipotong sudut melengkung
                        .border( // Memberi garis tepi gradasi pada gambar
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF7B2FF7)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop // Menyesuaikan gambar agar proporsional dan memenuhi area
                )

                Spacer(modifier = Modifier.height(16.dp)) // Jarak vertikal antar elemen

                Text( // Teks judul kartu
                    text = "KARTU MAHASISWA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D9FF),
                    letterSpacing = 2.sp // Jarak antar huruf
                )

                HorizontalDivider(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = Color(0xFF00D9FF).copy(alpha = 0.5f)
                )

                Column( // Kolom berisi data identitas
                    verticalArrangement = Arrangement.spacedBy(12.dp), // Jarak antar baris 12dp
                    horizontalAlignment = Alignment.Start, // Isi rata kiri
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row { // Baris untuk data nama
                        Text(
                            text = "Nama:", // Label
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8B8D1),
                            fontSize = 14.sp,
                            modifier = Modifier.width(100.dp) // Lebar tetap agar rapi
                        )
                        Text(
                            text = "Fiko Abdigusti", // Nilai data
                            textAlign = TextAlign.Start,
                            color = Color(0xFFE8E8F0),
                            fontSize = 14.sp
                        )
                    }
                    Row { // Baris untuk NIM
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
                    Row { // Baris untuk jurusan
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
                    Row { // Baris untuk universitas
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