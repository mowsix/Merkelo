package com.merqueloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.merqueloapp.ui.theme.MerqueloTheme
import com.merqueloapp.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MerqueloTheme {
                AppNavigation() // Toda la app vive en este NavHost
            }
        }
    }
}
