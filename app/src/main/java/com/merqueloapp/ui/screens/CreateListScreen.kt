// pantalla para crear una nueva lista de mercado
package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed
import kotlinx.coroutines.launch

// pantalla principal para crear una nueva lista de mercado
@Composable
fun CreateListScreen(
    currentRoute: String,              // ruta actual de navegacion
    onSelectTab: (String) -> Unit,     // callback para cambiar de pestaña
    vm: CreateListViewModel = viewModel() // viewmodel que maneja la logica
) {
    // carga las sugerencias de tiendas y productos al iniciar
    LaunchedEffect(Unit) { vm.loadSuggestions() }

    // estados para controlar la visualizacion de los dialogos
    var showStores by remember { mutableStateOf(false) }
    var showProductsForStore by remember { mutableStateOf<String?>(null) }

    // estados del viewmodel
    val name by vm.listName.collectAsState()
    val stores by vm.stores.collectAsState()
    val storeSuggestions by vm.storeSuggestions.collectAsState()
    val productSuggestions by vm.productSuggestions.collectAsState()

    // validaciones para habilitar/deshabilitar el guardado
    val hasAtLeastOneProduct = stores.any { it.products.isNotEmpty() }
    val canSave = name.isNotBlank() && stores.isNotEmpty() && hasAtLeastOneProduct

    // configuracion para mostrar mensajes al usuario
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // estructura principal de la pantalla
    Scaffold(
        // barra superior con titulo
        topBar = { AppTopBar(title = "Lista") },
        // barra inferior de navegacion
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        // configuracion para mostrar mensajes al usuario
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { inner ->
        // contenedor principal vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            
            // titulo de la pantalla
            Text(
                "Crea una nueva lista de mercado", 
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            // campo para el nombre de la lista
            OutlinedTextField(
                value = name,
                onValueChange = vm::setListName,
                label = { Text("Escribe el nombre de tu lista") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // mensaje de validacion
            if (name.isBlank()) {
                Text(
                    "Debes escribir un nombre.", 
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(20.dp))

            // boton para agregar una nueva tienda
            ElevatedButton(
                onClick = { showStores = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MerkeloRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Agregar tienda", 
                    fontWeight = FontWeight.Medium, 
                    fontSize = 18.sp
                )
            }
            // mensaje de validacion
            if (stores.isEmpty()) {
                Text(
                    "Agrega al menos una tienda.", 
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            // seccion de resumen de la lista
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (stores.isEmpty()) {
                    item {
                        Text(
                            "Sin tiendas ni productos aún.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(stores) { st ->
                        // Cabecera de tienda con botón editar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tienda: ${st.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { showProductsForStore = st.name }
                            ) { Text("Editar", color = MerkeloRed) }
                        }
                        // Productos
                        if (st.products.isEmpty()) {
                            Text("• (Sin productos)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            st.products.forEach { p ->
                                Text("• ${p.name} x${p.quantity}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // Guardar definitiva
            Button(
                onClick = {
                    if (!canSave) {
                        scope.launch {
                            snackbarHost.showSnackbar("Completa nombre, tiendas y al menos un producto.")
                        }
                    } else {
                        vm.save {
                            onSelectTab(Routes.HOME)
                        }
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MerkeloRed,
                    contentColor = Color.White
                )
            ) {
                Text("Finalizar", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    // D I Á L O G O S

    // Elegir o escribir UNA tienda por vez; no sobreescribe: agrega y abre productos de esa tienda
    if (showStores) {
        StorePickerSingleDialog(
            title = "Selecciona o escribe tu tienda",
            current = null,
            suggestions = storeSuggestions,
            onDismiss = { showStores = false },
            onCommit = { typedOrSelected ->
                val normalized = vm.addStore(typedOrSelected)  // 👈 agrega si no estaba
                showStores = false
                showProductsForStore = normalized               // 👈 abre productos de ESTA tienda
            }
        )
    }

    // Productos de una tienda (commit reemplaza el set de productos de esa tienda)
    showProductsForStore?.let { storeName ->
        val productsForStore = stores.find { it.name.equals(storeName, true) }?.products ?: emptyList()
        ProductPickerCommitDialog(
            storeName = storeName,
            initial = productsForStore,
            suggestions = productSuggestions,
            onDismiss = { showProductsForStore = null },
            onCommit = { chosen ->
                vm.setProductsForStore(storeName, chosen)
                showProductsForStore = null
            }
        )
    }
}

/* ---------- Diálogo: tienda única (input + sugerencias, con radio) ---------- */
@Composable
private fun StorePickerSingleDialog(
    title: String,
    current: String?,
    suggestions: List<String>,
    onDismiss: () -> Unit,
    onCommit: (String) -> Unit
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val final = input.text.trim().ifEmpty { selected ?: "" }
                if (final.isNotBlank()) onCommit(final) else onDismiss()
            }) { Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = MerkeloRed) } },
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe una tienda") },
                    singleLine = true,
                    trailingIcon = {
                        if (input.text.isNotEmpty()) TextButton({ input = TextFieldValue("") }) { Text("X") }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("Sugerencias", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                suggestions.forEach { s ->
                    val checked = selected?.equals(s, true) == true
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = checked,
                            onClick = { selected = s },
                            colors = RadioButtonDefaults.colors(selectedColor = MerkeloRed)
                        )
                        Text(s)
                    }
                }
            }
        }
    )
}

/* ---------- Diálogo: productos (lista local + commit) ---------- */
@Composable
private fun ProductPickerCommitDialog(
    storeName: String,
    initial: List<ProductSelection>,
    suggestions: List<String>,
    onDismiss: () -> Unit,
    onCommit: (List<ProductSelection>) -> Unit
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var qty by remember { mutableStateOf("1") }
    val local = remember(initial) { initial.map { it.copy() }.toMutableStateList() }

    fun toggleSuggestion(name: String) {
        val idx = local.indexOfFirst { it.name.equals(name, true) }
        if (idx >= 0) local.removeAt(idx) else local.add(ProductSelection(name, 1))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onCommit(local.toList()) }) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = MerkeloRed) } },
        title = { Text("Productos de $storeName") },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe un producto") },
                    singleLine = true,
                    trailingIcon = { if (input.text.isNotEmpty()) TextButton({ input = TextFieldValue("") }) { Text("X") } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it.filter { ch -> ch.isDigit() }.ifEmpty { "1" } },
                    label = { Text("Cantidad", fontWeight = FontWeight.Medium, fontSize = 18.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val p = input.text.trim()
                        val q = qty.toIntOrNull() ?: 1
                        if (p.isNotEmpty()) {
                            val idx = local.indexOfFirst { it.name.equals(p, true) }
                            if (idx >= 0) local[idx] = local[idx].copy(quantity = q.coerceAtLeast(1))
                            else local.add(ProductSelection(p, q.coerceAtLeast(1)))
                            input = TextFieldValue("")
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Añadir") }

                Spacer(Modifier.height(12.dp))
                Text("Sugerencias", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                suggestions.forEach { s ->
                    val checked = local.any { it.name.equals(s, true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { toggleSuggestion(s) },
                            colors = CheckboxDefaults.colors(checkedColor = MerkeloRed, checkmarkColor = Color.White)
                        )
                        Text(s)
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Seleccionados", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
                local.forEach {
                    Text("• ${it.name} x${it.quantity}", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }
            }
        }
    )
}
