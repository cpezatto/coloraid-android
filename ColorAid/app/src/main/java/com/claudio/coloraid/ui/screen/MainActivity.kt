package com.claudio.coloraid.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.claudio.coloraid.ui.theme.ColorAidTheme
import com.claudio.coloraid.viewmodel.MainViewModel
import com.claudio.coloraid.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = MainViewModelFactory(applicationContext)

        setContent {
            ColorAidTheme {
                val viewModel: MainViewModel = viewModel(factory = factory)
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
