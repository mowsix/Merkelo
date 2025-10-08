package com.merqueloapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.R
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed

@Composable
fun ListDetailScreen(
    listId: Long,
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: ListDetailViewModel = viewModel()
) {
    LaunchedEffect(listId) { vm.load(listId) }
    val detail by vm.detail.collectAsState()

    val snackbarHost = remember { SnackbarHostState() }
    var snackMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(snackMessage) {
        snackMessage?.let { snackbarHost.showSnackbar(it); snackMessage = null }
    }

    // Diálogos de confirmación
    var confirmDeleteStore by remember { mutableStateOf<String?>(null) }
    var confirmDeleteItem by remember { mutableStateOf<Pair<String, String>?>(null) } // storeName to productName

    Scaffold(
        topBar = { AppTopBar(title = detail?.listName ?: "Lista") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Imagen hero
            Image(
                painter = painterResource(id = R.drawable.marketlistimage),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            if (detail == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "Los Items de tu lista de ${detail!!.listName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Por cada tienda (cabecera con botón borrar)
                    items(detail!!.groups) { group ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = group.storeName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { confirmDeleteStore = group.storeName }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar tienda",
                                    tint = MerkeloRed
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))

                        // Items con botón borrar por producto
                        group.items.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "• ${entry.name} x${entry.quantity}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { confirmDeleteItem = group.storeName to entry.name }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar producto",
                                        tint = MerkeloRed
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (detail!!.groups.isEmpty()) {
                        item {
                            Text(
                                "Esta lista aún no tiene productos.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    /* --------- Confirmar borrado de TIENDA (y sus productos) --------- */
    confirmDeleteStore?.let { storeName ->
        AlertDialog(
            onDismissRequest = { confirmDeleteStore = null },
            title = { Text("Eliminar tienda") },
            text = { Text("¿Eliminar \"$storeName\" y todos sus productos de esta lista?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.removeStore(storeName)
                    confirmDeleteStore = null
                    snackMessage = "Tienda eliminada"
                }) { Text("Eliminar",color = MerkeloRed) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteStore = null }) { Text("Cancelar", color = MerkeloRed) }
            }
        )
    }

    /* --------- Confirmar borrado de PRODUCTO --------- */
    confirmDeleteItem?.let { (storeName, productName) ->
        AlertDialog(
            onDismissRequest = { confirmDeleteItem = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar \"$productName\" de la tienda \"$storeName\"?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.removeProduct(storeName, productName)
                    confirmDeleteItem = null
                    snackMessage = "Producto eliminado"
                }) { Text("Eliminar", color = MerkeloRed) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteItem = null }) { Text("Cancelar", color = MerkeloRed) }
            }
        )
    }
}
