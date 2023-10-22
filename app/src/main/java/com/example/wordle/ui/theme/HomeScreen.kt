
package com.example.wordle.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wordle.R
import com.example.wordle.data.Letters
import com.example.wordle.model.WordleViewModel
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
fun HomeScreen() {
    val model = WordleViewModel()
    val word by rememberSaveable {
        mutableStateOf(Letters.words5.random())
    }
    var isCardVisible by remember {
        mutableStateOf(false)
    }
    val letters by remember {
        mutableStateOf(WordleViewModel().letters1)
    }
    var updated by rememberSaveable {
        mutableStateOf(0)
    }
    LazyColumn (
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ){
        item {
            Column {
                HintRow {
                    isCardVisible = !isCardVisible
                }
                WordleTitle(modifier =  Modifier)
            }
            if (isCardVisible) {
                HintCard(onDismiss = { isCardVisible = false }, word = word)
            }
        }
        item { 
            Spacer(modifier = Modifier.height(25.dp))
        }
        item {
            val colors = WordleViewModel().getColors(word = word.first.uppercase(Locale.getDefault()))
            GameBoard(modifier = Modifier.fillMaxSize(), model.wordList, letters, updated, word, colors)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            InputWord(
                modifier = Modifier,
                userGuess = model.userGuess,
                onValueChange = { model.updateUserValue(it) },
                onEnter = {
                    model.addWordToList()
                    updated++
                    model.updateUserValue("")
                },
                letters = letters
            )
        }
    }
}

@Composable
fun GameBoard(modifier: Modifier, list: MutableList<String>, letters: Int, updated: Int, word: Pair<String, String>, colors: MutableList<Color>) {
    println(list)

    Column (modifier = modifier
        .size(400.dp)
    ){
            for (j in 1..letters) {
                Row (
                    Modifier
                        .weight(.2f)
                        .padding(
                            vertical = 4.dp
                        )
                ){
                    for (i in 1..letters) {
                        GameBoardUnit(modifier = Modifier.weight(.185f), list[j-1][i-1],i-1, word)
                        Spacer(modifier = Modifier.weight(.015f))
                    }
                }
            }
    }
}

@Composable
fun GameBoardUnit(modifier: Modifier, text: Char, idx: Int, word: Pair<String, String>) {
    val color = if (text.uppercaseChar() == word.first[idx].uppercaseChar()) {
        Color(0xFF79AC78)
    }else if (word.first.uppercase().contains(text.uppercaseChar())) {
        Color(0xFFF4CE14)
    }else {
        Color(0xFFD0D4CA)
    }
    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(color = color)
        .fillMaxHeight()
        .padding(end = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text.toString(), fontSize = 40.sp)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWord(modifier: Modifier, userGuess: String, onValueChange: (String) -> Unit, onEnter: () -> Unit, letters: Int) {
    var isError by rememberSaveable {
        mutableStateOf(false)
    }
    var wrongEntry by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = userGuess,
            onValueChange = {
                            onValueChange(it)
            },
            Modifier
                .padding(top = 16.dp)
                .border(border = BorderStroke(color = Color.Transparent, width = 0.dp)),
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = md_theme_dark_onPrimaryContainer),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .clickable {
                            if (userGuess.length != letters) {
                                isError = true
                                wrongEntry = true
                            } else {
                                isError = false
                                wrongEntry = false
                                onEnter()
                            }
                        }
                        .padding(5.dp)
                )
            },
            isError = isError
        )
        if (wrongEntry)
            Text(text = "Enter word of size: $letters !", color = Color.Red)

    }
}


@Composable
fun HintRow(onClick: () -> Unit) {
    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
        IconButton(onClick = {
            onClick()
        }) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Hint", tint = Color.White)
        }
    }

}
@Composable
fun HintCard(onDismiss: () -> Unit, word: Pair<String, String>) {
    var wantHint by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card (
            modifier = Modifier
                .padding(horizontal = 35.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = md_theme_dark_onPrimaryContainer)
        ){
            Column (
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
            ){
                if (!wantHint) {
                    Text(text = stringResource(id = R.string.want_hint))
                    Spacer(modifier = Modifier.height(30.dp))
                    Row (
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp), horizontalArrangement = Arrangement.End){
                        TextButton(onClick = { wantHint = true }) {
                            Text(text = "OK")
                        }
                        TextButton(onClick = { wantHint = false }) {
                            Text(text = "Cancel")
                        }
                    }
                }
                else {
                    Text(text = word.second)
                    Spacer(modifier = Modifier.height(30.dp))
                    Row (
                        Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End){
                        TextButton(
                            onClick = {
                                wantHint = false
                                onDismiss()
                            },
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun WordleTitle(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Wordle", color = Color.White, fontSize = 70.sp)
    }
}