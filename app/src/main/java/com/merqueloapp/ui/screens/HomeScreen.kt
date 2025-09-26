package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar

@Composable
fun HomeScreen(
    currentRoute: String,
    onCreateNew: () -> Unit,
    onOpenList: (String) -> Unit,
    onSelectTab: (String) -> Unit
) {
    val lists = remember {
        mutableStateListOf("Lista Mercado 1", "Lista Mercado 2", "Lista Desayuno")
    }

    Scaffold(
        topBar = { AppTopBar(title = "Inicio") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNew,
                text = { Text("Crear nuevo", fontSize = 18.sp) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(12.dp)) }
            item {
                Text(
                    text = "Tus mercados",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp
                )
            }
            items(lists) { title ->
                ElevatedCard(
                    onClick = { onOpenList(title) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 22.sp
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
