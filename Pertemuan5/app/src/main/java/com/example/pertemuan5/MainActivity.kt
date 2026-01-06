package com.example.pertemuan5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertemuan5.ui.theme.Pertemuan5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   FormMahasiswa(
                       modifier = Modifier.padding(innerPadding)
                   )
                }
            }
        }
    }
}

var listJurusan = listOf(
    "Teknik Informatika S1",
    "Komputerisasi Akuntansi D3",
    "Manajemen Informatika D3"
)

@Composable
fun FormMahasiswa(modifier: Modifier = Modifier) {
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var selectedJurusan by remember { mutableStateOf("") }
    var showDialog by remember {mutableStateOf("")}
    var dialogMessage by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Form Pendaftaran Mahasiswa",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(12.dp)

            )
            OutlinedTextField(
                value = nama,
                onValueChange = {nama = it},
                label = {Text(text = "Nama Anda")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = nim,
                onValueChange = {nim = it},
                label = {Text(text = "Nim Anda")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FormMahasiswaPreview() {
    Pertemuan5Theme {
        FormMahasiswa()
    }
}