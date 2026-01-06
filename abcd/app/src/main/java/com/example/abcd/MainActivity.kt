package com.example.abcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.abcd.ui.theme.AbcdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbcdTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Biodata()
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            Kolum()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Biodata() {
    Text(
        text = "Helo Dek",
        color = Color.Black,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun Kolum() {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ){
        Column(

        ) {
            Text("Rendi", color = Color.Black)
            Text("23010041", color = Color.Black)
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun KolumPreview () {
    AbcdTheme{}
    Kolum()
}

@Preview (showBackground = true)
@Composable
private fun BiodataPreview() {
    AbcdTheme {
        Biodata()
    }
}