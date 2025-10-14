// pantalla principal que muestra las listas de mercado recientes
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

/**
 * pantalla principal que muestra un resumen de las listas de mercado recientes
 * @param currentRoute ruta actual de navegacion
 * @param onCreateNew callback al presionar el boton de crear nueva lista
 * @param onOpenList callback al seleccionar una lista existente
 * @param onSelectTab callback para cambiar entre pestañas
 * @param vm viewmodel que maneja la logica de la pantalla
 */
@Composable
fun HomeScreen(
    currentRoute: String,
    onCreateNew: () -> Unit,
    onOpenList: (Long) -> Unit,
    onSelectTab: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    // estado que contiene las listas de mercado
    val lists by vm.lists.collectAsState()
    // obtiene solo las 5 listas mas recientes
    val lastFive = remember(lists) { lists.take(5) }

    // estructura principal de la pantalla
    Scaffold(
        // barra superior con titulo
        topBar = { AppTopBar(title = "Inicio") },
        // barra inferior de navegacion
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        // boton flotante para crear nueva lista
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNew,
                text = { Text("Crear nuevo", fontSize = 18.sp) },
                icon = { Icon(Icons.Default.Add, contentDescription = "crear nueva lista") },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { inner ->
        // lista desplazable que muestra las listas recientes
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // espaciado superior
            item { Spacer(Modifier.height(12.dp)) }
            
            // titulo de la seccion
            item {
                Text(
                    text = "Tus mercados recientes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            item { Spacer(Modifier.height(20.dp)) }
            
            // mensaje informativo
            item {
                Text(
                    text = "Para ver todas tus listas o editarlas navega a tu perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            item { Spacer(Modifier.height(20.dp)) }
            
            // lista de tarjetas de mercados recientes
            items(lastFive) { list ->
                // tarjeta que representa una lista de mercado
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = com.merqueloapp.ui.theme.MerkeloRed,
                        contentColor = Color.White
                    ),
                    onClick = { onOpenList(list.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .heightIn(min = 70.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // nombre de la lista de mercado
                        Text(
                            color = Color.White,
                            text = list.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            // mensaje cuando no hay listas
            if (lastFive.isEmpty()) {
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    Text(
                        "Aún no tienes listas. ¡Crea la primera!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // espaciado inferior para el boton flotante
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
