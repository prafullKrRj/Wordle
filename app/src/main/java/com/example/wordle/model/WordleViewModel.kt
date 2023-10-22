package com.example.wordle.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.wordle.data.Letters
import com.example.wordle.data.WordleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class WordleViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(WordleUiState())
    val uiState: StateFlow<WordleUiState> = _uiState.asStateFlow()
    var userGuess by mutableStateOf("")
        private set
    var letters1 by mutableStateOf(5)
    var idx by mutableStateOf(0)
    var wordList: MutableList<String> by mutableStateOf(mutableListOf())

    var word by mutableStateOf(Pair("", ""))

    init {
        newGame(letters1)
        word = Letters.words5.random()
    }
    private fun newGame(size: Int) {
        wordList.clear()
        letters1 = size
        println(letters1)
        for (i in 1..size) {
            wordList.add(" ".repeat(size))
        }
        println(wordList)
    }

    fun updateUserValue(value: String) {
        userGuess = value
    }
    fun addWordToList() : Boolean{
        if (idx < letters1) {
            wordList[idx++] = userGuess.uppercase(Locale.getDefault())
            if (userGuess == word.first) {
                return true;
            }
            return false;
        }
        else {
            return false;
        }
        println(wordList)
    }

    fun getColors(word: String): MutableList<Color> {
        val list: MutableList<Color> = mutableListOf()
        println(word)
        for (i in 1..5) {
            val char1 = wordList[i-1][i-1].uppercaseChar()
            val char2 = word[i-1].uppercaseChar()
            if (char1 == char2) {
                list.add(Color(0xFF79AC78))
            }else if (word.uppercase(Locale.getDefault()).contains(char1)) {
                list.add(Color(0xFFF4CE14))
            } else {
                list.add(Color(0xFFD0D4CA))
            }
        }
        return list;
    }
}