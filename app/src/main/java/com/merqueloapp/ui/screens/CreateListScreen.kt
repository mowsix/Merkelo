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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed
import kotlinx.coroutines.launch

@Composable
fun CreateListScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: CreateListViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.loadSuggestions() }

    var showStores by remember { mutableStateOf(false) }
    var showProductsForStore by remember { mutableStateOf<String?>(null) }

    // Estados del VM
    val name by vm.listName.collectAsState()
    val stores by vm.stores.collectAsState()
    val storeSuggestions by vm.storeSuggestions.collectAsState()
    val productSuggestions by vm.productSuggestions.collectAsState()

    // Validaciones
    val hasAtLeastOneProduct = stores.any { it.products.isNotEmpty() }
    val canSave = name.isNotBlank() && stores.isNotEmpty() && hasAtLeastOneProduct

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppTopBar(title = "Lista") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Crea una nueva lista de mercado", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(20.dp))

            // Nombre de la lista
            OutlinedTextField(
                value = name,
                onValueChange = vm::setListName,
                label = { Text("Escribe el nombre de tu lista") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (name.isBlank()) {
                Text("Debes escribir un nombre.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))

            // Tienda (selección única)
            ElevatedButton(
                onClick = { showStores = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MerkeloRed,
                    contentColor = Color.White
                )
            ) {
                Text("Selecciona o escribe tu tienda", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
            if (stores.isEmpty()) {
                Text("Agrega una tienda.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))

            // Productos (por la tienda seleccionada)
            if (stores.isNotEmpty()) {
                ElevatedButton(
                    onClick = { showProductsForStore = stores.first().name },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MerkeloRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Selecciona o escribe tus Productos", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }
                if (!hasAtLeastOneProduct) {
                    Text("Agrega al menos un producto.", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Resumen SIEMPRE visible
            Text(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                text = "Resumen", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (stores.isEmpty()) {
                    item { Text("Sin tiendas ni productos aún.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(stores) { st ->
                        Text(
                            text = "Tienda: ${st.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        st.products.forEach { p ->
                            Text(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                text = "• ${p.name} x${p.quantity}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // Finalizar / Guardar definitiva
            Button(
                onClick = {
                    if (!canSave) {
                        scope.launch {
                            snackbarHost.showSnackbar("Completa nombre, tienda y al menos un producto.")
                        }
                    } else {
                        vm.save {
                            onSelectTab(Routes.HOME)
                        }
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
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

    // Tiendas (selección única con commit en Guardar)
    if (showStores) {
        StorePickerDialog(
            currentStore = stores.firstOrNull()?.name,
            suggestions = storeSuggestions,
            onDismiss = { showStores = false },
            onCommitStore = { selectedOrTyped ->
                vm.setSingleStore(selectedOrTyped)
                showStores = false
                // opcional: abrir de una vez productos
                // showProductsForStore = selectedOrTyped
            }
        )
    }

    // Productos: commit en Guardar
    showProductsForStore?.let { storeName ->
        val selectedProducts = stores.find { it.name == storeName }?.products ?: emptyList()
        ProductPickerDialog(
            storeName = storeName,
            initial = selectedProducts,
            suggestions = productSuggestions,
            onDismiss = { showProductsForStore = null },
            onCommit = { finalProducts ->
                vm.setProductsForStore(storeName, finalProducts)
                showProductsForStore = null
            }
        )
    }
}

/* --------------------  Diálogo: Seleccionar UNA tienda  -------------------- */
@Composable
private fun StorePickerDialog(
    currentStore: String?,
    suggestions: List<String>,
    onDismiss: () -> Unit,
    onCommitStore: (String) -> Unit
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf(currentStore) } // estado local para feedback inmediato

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val typed = input.text.trim()
                val final = if (typed.isNotEmpty()) typed else selected
                if (!final.isNullOrBlank()) onCommitStore(final)
                else onDismiss()
            }) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        title = { Text("Selecciona o escribe tu tienda") },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Ej: La Vaquita") },
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
                    val checked = selected?.equals(s, ignoreCase = true) == true
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                selected = if (isChecked) s else null
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MerkeloRed,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(s)
                    }
                }
            }
        }
    )
}

/* --------------------  Diálogo: Productos (lista local + commit)  -------------------- */
@Composable
private fun ProductPickerDialog(
    storeName: String,
    initial: List<ProductSelection>,
    suggestions: List<String>,
    onDismiss: () -> Unit,
    onCommit: (List<ProductSelection>) -> Unit
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var qty by remember { mutableStateOf("1") }

    // lista local editable para feedback inmediato
    val local = remember(initial) { initial.map { it.copy() }.toMutableStateList() }

    fun toggleSuggestion(name: String) {
        val idx = local.indexOfFirst { it.name.equals(name, true) }
        if (idx >= 0) local.removeAt(idx)
        else local.add(ProductSelection(name, 1))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onCommit(local.toList()) }) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        title = { Text("Productos de $storeName") },
        text = {
            Column {
                // Escribir producto y cantidad + botón Añadir
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe un producto") },
                    singleLine = true,
                    trailingIcon = {
                        if (input.text.isNotEmpty()) TextButton({ input = TextFieldValue("") }) { Text("X") }
                    },
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
                            colors = CheckboxDefaults.colors(
                                checkedColor = MerkeloRed,
                                checkmarkColor = Color.White
                            )
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
