package com.example.wordle.data

data class WordleUiState(
    val word: Pair<String, String> = Letters.words5.random()
)