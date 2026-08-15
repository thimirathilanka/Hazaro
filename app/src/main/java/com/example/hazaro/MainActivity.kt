package com.example.hazaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hazaro.ui.navigation.HazaroNavGraph
import com.example.hazaro.ui.theme.HazaroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HazaroTheme {
                HazaroNavGraph()
            }
        }
    }
}
