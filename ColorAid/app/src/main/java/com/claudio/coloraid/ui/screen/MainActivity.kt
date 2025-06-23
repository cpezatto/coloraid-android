package com.claudio.coloraid.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.claudio.coloraid.ui.theme.ColorAidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ColorAidTheme {
                MainScreen()
            }
        }
    }
}
