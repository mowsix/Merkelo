package com.merqueloapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed
import com.merqueloapp.ui.theme.White100

@Composable
fun StoresScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: StoresViewModel = viewModel(),
    onViewMap: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val favorites by vm.favorites.collectAsState()        // nombres desde Room

    val snackbarHost = remember { SnackbarHostState() }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    // Mostrar snackbar cuando cambie el mensaje
    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHost.showSnackbar(it)
            snackMessage = null
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Tiendas") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        snackbarHost = { SnackbarHost(hostState = snackbarHost) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Agrega tus tiendas favoritas", fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre de la tienda (ej. La Vaquita)") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Sugeridas (quemadas)
            Text("Sugeridas", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = SolidColor(Color.LightGray),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(vertical = 6.dp)
            ) {
                vm.defaultStores.forEach { store ->
                    val alreadyFav = favorites.any { it.equals(store, ignoreCase = true) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(store, fontSize = 18.sp)
                        OutlinedIconButton(
                            onClick = {
                                if (!alreadyFav) {
                                    vm.addFavorite(store)
                                    snackMessage = "“$store” añadida a favoritas"
                                }
                            },
                            enabled = !alreadyFav
                        ) {
                            if (alreadyFav) {
                                Icon(Icons.Default.Check, contentDescription = "Agregada", tint = MerkeloRed)
                            } else {
                                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tus favoritas actuales
            if (favorites.isNotEmpty()) {
                Text("Tus favoritas", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                favorites.forEach { s -> Text("• $s") }
                Spacer(Modifier.height(8.dp))
            }

            // Guardar: si hay texto, lo añade; siempre navega a Perfil
            Button(
                onClick = {
                    val typed = input.trim()
                    if (typed.isNotEmpty()) {
                        vm.addFavorite(typed)
                        input = ""
                    }
                    onSelectTab(Routes.PROFILE) // Ir a Usuario/Perfil
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MerkeloRed,
                    contentColor = White100
                ),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Text("Guardar", fontSize = 18.sp)
            }

            Spacer(Modifier.height(12.dp))

            // (Opcional) Mapa: pendiente implementar
            OutlinedButton(
                onClick = { onViewMap.invoke() },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.85f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MerkeloRed,
                    contentColor = White100
                ),
            ) {
                Text("Visualizar en el mapa", fontSize = 18.sp)
            }
        }
    }
}
