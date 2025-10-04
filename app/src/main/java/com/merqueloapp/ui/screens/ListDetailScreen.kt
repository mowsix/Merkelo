package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.R
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun ListDetailScreen(
    listId: Long,
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: ListDetailViewModel = viewModel()
) {
    LaunchedEffect(listId) { vm.load(listId) }
    val detail by vm.detail.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = detail?.listName ?: "Lista") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Imagen hero
            Image(
                painter = painterResource(id = R.drawable.marketlistimage
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            if (detail == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                // Contenido
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "Los Items de tu lista de "+detail!!.listName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Por cada tienda
                    items(detail!!.groups) { group ->
                        Text(
                            text = group.storeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        group.items.forEach { entry ->
                            Text("• ${entry.name} x${entry.quantity}", style = MaterialTheme.typography.bodyMedium)
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
}
