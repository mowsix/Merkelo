package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.merqueloapp.R
import com.merqueloapp.data.local.MarketListEntity

@Composable
fun AddProductScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    vm: AddProductViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.loadInitial() }

    val lists by vm.lists.collectAsState()
    val selectedList by vm.selectedList.collectAsState()
    val storeName by vm.storeName.collectAsState()
    val storeSuggestions by vm.storeSuggestions.collectAsState()
    val productSuggestions by vm.productSuggestions.collectAsState()
    val products by vm.products.collectAsState()

    var showListPicker by remember { mutableStateOf(false) }
    var showStorePicker by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val canSave = selectedList != null && !storeName.isNullOrBlank() && products.isNotEmpty()

    Scaffold(
        topBar = { AppTopBar(title = "Agregar producto") },
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
                painter = painterResource(id = R.drawable.market2),
                contentDescription = "Imagen Mercado",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            // Contenido principal con espacio para el resumen (scroll)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Selecciona la lista y agrega productos",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(16.dp))

                // Seleccionar lista
                ElevatedButton(
                    onClick = { showListPicker = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MerkeloRed, contentColor = Color.White
                    )
                ) {
                    Text(
                        text = selectedList?.name ?: "Selecciona una lista",
                        fontWeight = FontWeight.Medium, fontSize = 18.sp
                    )
                }
                if (selectedList == null) {
                    Text("Debes seleccionar una lista.", color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // Seleccionar o escribir tienda (una)
                if (selectedList != null) {
                    ElevatedButton(
                        onClick = { showStorePicker = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MerkeloRed, contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = storeName ?: "Selecciona o escribe tu tienda",
                            fontWeight = FontWeight.Medium, fontSize = 18.sp
                        )
                    }
                    if (storeName.isNullOrBlank()) {
                        Text("Debes elegir una tienda.", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Productos
                if (selectedList != null && !storeName.isNullOrBlank()) {
                    ElevatedButton(
                        onClick = { showProductPicker = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MerkeloRed, contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Selecciona o escribe tus Productos",
                            fontWeight = FontWeight.Medium, fontSize = 18.sp
                        )
                    }
                    if (products.isEmpty()) {
                        Text("Agrega al menos un producto.", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Resumen (con altura propia y scroll si hace falta)
                Text("Resumen", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (selectedList == null) {
                        item {
                            Text(
                                "Sin selección aún.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        item { Text("Lista: ${selectedList!!.name}", fontWeight = FontWeight.Medium) }
                        item { Text("Tienda: ${storeName ?: "-"}", fontWeight = FontWeight.Medium) }
                        items(products) { p ->
                            Text("• ${p.name} x${p.quantity}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Botón inferior fijo
            Button(
                onClick = {
                    if (!canSave) {
                        scope.launch { snackbarHost.showSnackbar("Completa lista, tienda y productos.") }
                    } else {
                        vm.save { onSelectTab(Routes.HOME) }
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MerkeloRed, contentColor = Color.White
                )
            ) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    // ==== Diálogos ====

    if (showListPicker) {
        ListPickerDialog(
            lists = lists,
            current = selectedList,
            onDismiss = { showListPicker = false },
            onCommit = { chosen ->
                vm.selectList(chosen)
                showListPicker = false
            }
        )
    }

    if (showStorePicker && selectedList != null) {
        StorePickerSingleDialog(
            title = "Tienda para ${selectedList!!.name}",
            current = storeName,
            suggestions = storeSuggestions,
            onDismiss = { showStorePicker = false },
            onCommit = { store ->
                vm.setStore(store)
                showStorePicker = false
            }
        )
    }

    if (showProductPicker && selectedList != null && !storeName.isNullOrBlank()) {
        ProductPickerCommitDialog(
            storeName = storeName!!,
            initial = products,
            suggestions = productSuggestions,
            onDismiss = { showProductPicker = false },
            onCommit = { vm.setProducts(it); showProductPicker = false }
        )
    }
}

/* ----------------  Diálogo: seleccionar lista (radio/simple)  ---------------- */
@Composable
private fun ListPickerDialog(
    lists: List<MarketListEntity>,
    current: MarketListEntity?,
    onDismiss: () -> Unit,
    onCommit: (MarketListEntity) -> Unit
) {
    var selected by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { selected?.let(onCommit) ?: onDismiss() }) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = MerkeloRed) } },
        title = { Text("Selecciona una lista") },
        text = {
            Column {
                if (lists.isEmpty()) {
                    Text("No hay listas. Crea una primero.")
                } else {
                    lists.forEach { l ->
                        val checked = selected?.id == l.id
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { if (it) selected = l else selected = null },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MerkeloRed,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(l.name)
                        }
                    }
                }
            }
        }
    )
}

/* -------------  Diálogo: tienda única (input + sugerencias) ------------- */
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
            }) {
                Text("Guardar", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MerkeloRed)
            }
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
                suggestions.forEach { s ->
                    val checked = selected?.equals(s, true) == true
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked -> selected = if (isChecked) s else null },
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

/* -------------  Diálogo: productos (lista local + commit) ------------- */
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
                            if (idx >= 0) {
                                local[idx] = local[idx].copy(quantity = q.coerceAtLeast(1))
                            } else {
                                local.add(ProductSelection(p, q.coerceAtLeast(1)))
                            }
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
