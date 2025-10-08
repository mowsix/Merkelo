package com.merqueloapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    onEditList: (Long) -> Unit,
    onEditStore: (String) -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val allLists by vm.allLists.collectAsState()
    val favoriteStores by vm.favoriteStores.collectAsState()

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // estados para confirmación
    var confirmDeleteList by remember { mutableStateOf<Long?>(null) }
    var confirmDeleteStore by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Perfil") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        snackbarHost = { SnackbarHost(snackbarHost) }
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

                // Listas creadas
                item { SectionTitle("Todas las listas que has creado") }
                items(allLists) { lista ->
                    ProfileCard(
                        title = lista.name,
                        onEditClick = { onEditList(lista.id) },
                        onDeleteClick = { confirmDeleteList = lista.id }
                    )
                }
                if (allLists.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no has creado listas.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }

                // Favoritas
                item { SectionTitle("Todas tus tiendas favoritas") }

                if (favoriteStores.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no tienes tiendas favoritas. Ve a la pestaña Tiendas para agregarlas.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(favoriteStores) { store ->
                        StoresCard(
                            title = store,
                            onEditStore = { onEditStore(store) },
                            onDeleteStore = { confirmDeleteStore = store }
                        )
                    }
                }
            }
        }
    }

    // Diálogo: confirmar borrar lista
    confirmDeleteList?.let { listId ->
        AlertDialog(
            onDismissRequest = { confirmDeleteList = null },
            title = { Text("Eliminar lista") },
            text = { Text("¿Seguro que deseas eliminar esta lista por completo?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteList(listId) {
                        confirmDeleteList = null
                        // feedback
                        scope.launch { snackbarHost.showSnackbar("Lista eliminada") }
                    }
                }) { Text("Eliminar", color = MerkeloRed, fontSize = 18.sp) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteList = null }) { Text("Cancelar", color = MerkeloRed, fontSize = 18.sp) }
            }
        )
    }

    // Diálogo: confirmar borrar tienda favorita
    confirmDeleteStore?.let { storeName ->
        AlertDialog(
            onDismissRequest = { confirmDeleteStore = null },
            title = { Text("Eliminar tienda favorita") },
            text = { Text("¿Quitar \"$storeName\" de tus tiendas favoritas?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteFavoriteStore(storeName) {
                        confirmDeleteStore = null
                        scope.launch { snackbarHost.showSnackbar("Tienda eliminada de favoritos") }
                    }
                }) { Text("Eliminar", color = MerkeloRed, fontSize = 18.sp) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteStore = null }) { Text("Cancelar", color = MerkeloRed, fontSize = 18.sp) }
            }
        )
    }
}

/* ---------- UI helpers ---------- */

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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar lista",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun StoresCard(
    title: String,
    onEditStore: () -> Unit,
    onDeleteStore: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditStore) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tienda",
                    tint = Color.White
                )
            }
            IconButton(onClick = onDeleteStore) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tienda favorita",
                    tint = Color.White
                )
            }
        }
    }
}
