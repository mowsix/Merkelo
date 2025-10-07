package com.merqueloapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloDarkRed
import com.merqueloapp.ui.theme.MerkeloRed
import com.merqueloapp.ui.theme.White100

@Composable
fun ProfileScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    onEditList: (Long) -> Unit,                  // <-- callback para ir a AddProduct con esa lista
    vm: ProfileViewModel = viewModel()
) {
    val allLists by vm.allLists.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Perfil") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(White100)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado
                item {
                    Text(
                        text = "Historial de listas",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Historial de listas (todas)
                item { SectionTitle("Todas las listas que has creado") }

                items(allLists) { lista ->
                    ProfileCard(
                        title = lista.name,
                        onEditClick = { onEditList(lista.id) }   // lápiz → AddProduct con listId
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }

                // Historial de supermercados (TODO)
                item { SectionTitle("Todas tus tiendas favoritas") }
                item {
                    Text(
                        text = "TODO: Mostrar tiendas frecuentes/recientes",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/* ---------- UI helpers en el mismo archivo para evitar "Unresolved reference" ---------- */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MerkeloDarkRed,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ProfileCard(
    title: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MerkeloRed),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White
                )
            }
        }
    }
}
