package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar

@Composable
fun MapScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit
) {
    Scaffold(
    topBar = { AppTopBar(title = "Mapa") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Box(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentAlignment = Alignment.Center
        ) {
            // Empty content
        }
    }
}
