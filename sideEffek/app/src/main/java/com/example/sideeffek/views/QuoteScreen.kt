package com.example.sideeffek.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sideeffek.model.Quote
import com.example.sideeffek.viewModel.QuoteUiState
import com.example.sideeffek.viewModel.QuoteViewModel
import kotlinx.coroutines.launch

@Composable
fun QuoteScreen (viewModel: QuoteViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchNewQuote()
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = uiState) {
            is QuoteUiState.Loading -> {
                CircularProgressIndicator()
            }
            is QuoteUiState.Success -> {
            QuoteCard(quote = state.quote)
            }
            is QuoteUiState.Error -> {
                Text(text = "Error: ${state.message}")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
            scope.launch {
                viewModel.fetchNewQuote()
            }
        }
        ) {
            Text("Refresh Quote")
        }
    }
}

@Composable
fun QuoteCard(quote: Quote) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)){
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "\"${quote.quote}\"",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "- ${quote.author}",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )
        }
    }
}