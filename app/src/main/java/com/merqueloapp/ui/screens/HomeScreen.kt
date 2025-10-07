package com.merqueloapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloDarkRed
import com.merqueloapp.ui.theme.MerkeloRed

@Composable
fun HomeScreen(
    currentRoute: String,
    onCreateNew: () -> Unit,
    onOpenList: (Long) -> Unit,
    onSelectTab: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val lists by vm.lists.collectAsState()
    val lastFive = remember(lists) { lists.take(5) }   // 👈 limitar a 5 ya ordenadas por DESC

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
                    text = "Tus mercados recientes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    textAlign= TextAlign.Center

                )}
                item { Spacer(Modifier.height(20.dp)) }
            item{
                Text(
                    text = "Para ver todas tus listas o editarlas navega a tu perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    textAlign= TextAlign.Center
                )

            }
            item { Spacer(Modifier.height(20.dp)) }
            items(lastFive) { list ->
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = com.merqueloapp.ui.theme.MerkeloRed,
                        contentColor = Color.White
                    ),
                    onClick = { onOpenList(list.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .heightIn(min = 70.dp) // 👈 más alta
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp), // 👈 más padding interno
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            color = Color.White,
                            text = list.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 30.sp,          // 👈 más grande
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (lastFive.isEmpty()) {
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    Text(
                        "Aún no tienes listas. ¡Crea la primera!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
