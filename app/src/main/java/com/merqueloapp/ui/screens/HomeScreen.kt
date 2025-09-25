package com.merqueloapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqueloapp.R
import com.merqueloapp.navigation.Routes

@Composable
fun HomeScreen(
    onCreateNew: () -> Unit,
    onOpenList: (String) -> Unit,
    onTabSelected: (String) -> Unit
) {
    val lists = remember {
        mutableStateListOf("Lista Mercado 1", "Lista Mercado 2", "Lista Desayuno")
    }

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = {
            HomeBottomBar(
                current = Routes.HOME,
                onSelect = onTabSelected
            )
        },
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(lists) { title ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    onClick = { onOpenList(title) }
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
            item { Spacer(Modifier.height(80.dp)) } // espacio para el FAB
        }
    }
}

@Composable
private fun HomeTopBar() {
    Surface(shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            // Logo izquierda
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
            //Spacer(Modifier.width(12.dp))
            // Título centrado relativo
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(8f).fillMaxWidth().offset(x=-14.dp)
            )
            //Spacer(Modifier.width(36.dp)) // simetría con el logo
        }
    }
}

@Composable
private fun HomeBottomBar(
    current: String,
    onSelect: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == Routes.HOME,
            onClick = { onSelect(Routes.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = current == Routes.MARKET,
            onClick = { onSelect(Routes.MARKET) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Mercados") },
            label = { Text("Listas") }
        )
        NavigationBarItem(
            selected = current == Routes.STORES,
            onClick = { onSelect(Routes.STORES) },
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Tiendas") },
            label = { Text("Tiendas") }
        )
        NavigationBarItem(
            selected = current == Routes.PROFILE,
            onClick = { onSelect(Routes.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
