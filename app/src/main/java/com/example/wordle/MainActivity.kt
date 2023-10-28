package com.example.wordle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.wordle.ui.theme.HomeScreen
import com.example.wordle.ui.theme.WordleTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordleTheme (darkTheme = false){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSize = LocalConfiguration.current
                    HomeScreen()
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}

