package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar

@Composable
fun StoresScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(title = "Tiendas") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Box(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp)
            ) {
                Text("Agrega tus tiendas favoritas")
                Spacer(modifier = Modifier.height(16.dp))
                val (text, setText) = remember { mutableStateOf("") }
                OutlinedTextField(
                    value = text,
                    onValueChange = setText,
                    modifier = Modifier,
                    placeholder = { Text("Nombre de la tienda") }
                )
                // Contenido futuro de Tiendas
            }
        }
    }
}
