package com.merqueloapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager


@Composable
fun StoresScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: StoresViewModel = viewModel(),
    onViewMap: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val favorites by vm.favorites.collectAsState() // nombres desde Room

    val snackbarHost = remember { SnackbarHostState() }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    // Mostrar snackbar cuando cambie el mensaje
    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHost.showSnackbar(it)
            snackMessage = null
        }
    }

    // Favoritas personalizadas = favoritas que no están dentro de las sugeridas por defecto
    val customFavorites = remember(favorites, vm.defaultStores) {
        val defaultsLower = vm.defaultStores.map { it.lowercase() }.toSet()
        favorites.filter { it.lowercase() !in defaultsLower }.sorted()
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

            val focusManager = LocalFocusManager.current

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre de la tienda (ej. La Vaquita)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val typed = input.trim()
                        if (typed.isNotEmpty()) {
                            vm.addFavorite(typed)
                            snackMessage = "“$typed” añadida a favoritas"
                            input = ""
                        }
                        focusManager.clearFocus() // cierra el teclado
                    }
                )
            )


            Spacer(Modifier.height(16.dp))

            // Caja única: sugeridas + favoritas personalizadas
            Text("Sugeridas y tus agregadas", fontWeight = FontWeight.Medium)
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
                // 1) SUGERIDAS por defecto (D1, Ara, etc.)
                vm.defaultStores.forEach { store ->
                    val alreadyFav = favorites.any { it.equals(store, ignoreCase = true) }
                    StoreRow(
                        name = store,
                        right = {
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
                    )
                }

                // Separador visual sólo si hay personalizadas
                if (customFavorites.isNotEmpty()) {
                    Divider(Modifier.padding(vertical = 4.dp))
                    Text(
                        "Tus agregadas",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 2) FAVORITAS personalizadas (las que el usuario escribió)
                customFavorites.forEach { store ->
                    // Ya son favoritas, las mostramos con un check (deshabilitado)
                    StoreRow(
                        name = store,
                        right = {
                            OutlinedIconButton(onClick = { /* ya es favorita */ }, enabled = false) {
                                Icon(Icons.Default.Check, contentDescription = "Agregada", tint = MerkeloRed)
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Guardar = añade lo que está en el input (si hay) y va a Perfil
            Button(
                onClick = {
                    val typed = input.trim()
                    if (typed.isNotEmpty()) {
                        vm.addFavorite(typed)
                        snackMessage = "“$typed” añadida a favoritas"
                        input = ""
                    }
                    onSelectTab(Routes.PROFILE) // Ir a Perfil
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

            OutlinedButton(
                onClick = { onViewMap.invoke() },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.85f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MerkeloRed,
                    contentColor = White100
                )
            ) {
                Text("Visualizar en el mapa", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun StoreRow(
    name: String,
    right: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 18.sp)
        right()
    }
}
