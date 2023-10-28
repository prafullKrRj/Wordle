package com.example.wordle.data

data class WordleUiState(
    val word: Pair<String, String> = Letters.words5.random(),
    var idx: Int = 0,
    var list: MutableList<String> = mutableListOf()
)