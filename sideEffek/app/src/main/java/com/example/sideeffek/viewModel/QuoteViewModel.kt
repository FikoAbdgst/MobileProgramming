package com.example.sideeffek.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sideeffek.model.Quote
import com.example.sideeffek.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface QuoteUiState {
    object Loading : QuoteUiState
    data class Success(val quote: Quote) : QuoteUiState
    data class Error(val message: String?) : QuoteUiState
}

class QuoteViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    fun fetchNewQuote() {
        viewModelScope.launch {
            _uiState.value = QuoteUiState.Loading
            try {
                val quote = QuoteRepository().getRandomQuote()
                _uiState.value = QuoteUiState.Success(quote)
            }catch (e: Exception) {
                _uiState.value = QuoteUiState.Error(e.message ?: "Gagal Memuat Data")
            }
        }
    }
}