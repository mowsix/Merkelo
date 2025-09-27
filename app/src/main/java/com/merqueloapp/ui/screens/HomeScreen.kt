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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar

@Composable
fun HomeScreen(
    currentRoute: String,
    onCreateNew: () -> Unit,
    onOpenList: (String) -> Unit,  // por ahora pasamos el nombre; luego usaremos id
    onSelectTab: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val lists by vm.lists.collectAsState()

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
            items(lists) { list ->
                ElevatedCard(
                    onClick = { onOpenList(list.name) },
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
                            text = list.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 22.sp
                        )
                    }
                }
            }
            if (lists.isEmpty()) {
                item { Spacer(Modifier.height(20.dp)) }
                item { Text("Aún no tienes listas. ¡Crea la primera!", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

