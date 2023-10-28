
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
import androidx.compose.runtime.collectAsState
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
import kotlin.system.exitProcess

@ExperimentalMaterial3Api
@Composable
fun HomeScreen() {
    var newGame by rememberSaveable {
        mutableStateOf(true)
    }
    if (newGame != !newGame) {
        val model = WordleViewModel()
        val uiState = model.uiState.collectAsState()
        var word by rememberSaveable {
            mutableStateOf(Letters.words5.random())
        }
        var isCardVisible by rememberSaveable {
            mutableStateOf(false)
        }
        val letters by rememberSaveable {
            mutableStateOf(5)
        }
        var updated by rememberSaveable {
            mutableStateOf(0)
        }
        var isFinalized by rememberSaveable {
            mutableStateOf(false)
        }
        var isRight by rememberSaveable {
            mutableStateOf(false)
        }
        var count by rememberSaveable {
            mutableStateOf(1)
        }
        var changeGame by rememberSaveable {
            mutableStateOf(true)
        }
        LazyColumn (
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ){
            if (isFinalized) {
                item {
                    GameFinish(onEnter = {
                        println("its")
                        if (it == 1) {
                            exitProcess(0)
                        }
                        else {
                            isFinalized = false
                            WordleViewModel().newGame(5)
                            updated++
                            changeGame = !changeGame
                            count = 1
                            word = Letters.words5.random()
                            isRight = false
                            newGame = !newGame
                        }
                    },
                        isRight, word = word.first.uppercase())
                }
            }
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
                GameBoard(modifier = Modifier.fillMaxSize(), uiState.value.list, letters, updated, word)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                InputWord(
                    modifier = Modifier,
                    userGuess = model.userGuess,
                    onValueChange = { model.updateUserValue(it) },
                    onEnter = {userGuess ->
                        model.addWordToList(userGuess, word.first)
                        updated++
                        if (userGuess.uppercase() == word.first.uppercase()) {
                            isRight = true
                            isFinalized = true
                        }else if (count == 5) {
                            isFinalized = true
                        }
                        count++
                        model.updateUserValue("")
                    },
                    letters = letters
                )

            }
        }
    }
}

@Composable
fun GameBoard(modifier: Modifier, list: MutableList<String>, letters: Int, updated: Int, word: Pair<String, String>) {
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
    }else if (text == ' ') {
        Color(0xFFA0E9FF)
    }
    else {
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
fun InputWord(modifier: Modifier, userGuess: String, onValueChange: (String) -> Unit, onEnter: (String) -> Unit, letters: Int) {
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
            colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = md_theme_dark_onPrimaryContainer, textColor =  Color.Black, cursorColor = md_theme_light_primary),
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
                                onEnter(userGuess)
                            }
                        }
                        .padding(5.dp),
                    tint = Color(0xff040D12)
                )
            },
            isError = isError,
            singleLine = true,

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
fun GameFinish(onEnter: (Int) -> Unit, isRight: Boolean, word: String) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Card (
            modifier = Modifier
        ){
            if (isRight) {
                FinishDialogInterface(word = word, label = R.string.hurray) {
                    onEnter(it)
                }
            }
            else {
                FinishDialogInterface(word = word, label = R.string.sorry) {
                    onEnter(it)
                }
            }
        }
    }
}

@Composable
fun FinishDialogInterface(word: String, label: Int, onEnter: (Int) -> Unit) {
    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)){
        Row (Modifier.fillMaxWidth()){
            repeat(5) {
                Box(modifier = Modifier
                    .weight(.185f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = Color(0xFF79AC78))
                    .padding(end = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = word[it].toString(), fontSize = 40.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.weight(.015f))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = stringResource(id = label), fontSize = 40.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(32.dp))
        Row (
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.End){
            TextButton(onClick = { onEnter(1) }) {
                Text(text = stringResource(id = R.string.exit), color = Color.Black)
            }
            TextButton(onClick = { onEnter(2) }) {
                Text(text = stringResource(id = R.string.new_game), color = Color.Black)
            }
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
                    Text(text = stringResource(id = R.string.want_hint), color = Color.Black)
                    Spacer(modifier = Modifier.height(30.dp))
                    Row (
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp), horizontalArrangement = Arrangement.End){
                        TextButton(onClick = { wantHint = true }) {
                            Text(text = "OK", color = Color.Black)
                        }
                        TextButton(onClick = { wantHint = false }) {
                            Text(text = "Cancel", color = Color.Black)
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