package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
// ...existing code...
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed
import com.merqueloapp.ui.theme.White100

@Composable
fun StoresScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(title = "Tiendas") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Box(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp)
            ) {
                Text("Agrega tus tiendas favoritas")
                Spacer(modifier = Modifier.height(16.dp))
                var text by remember { mutableStateOf("") }
                var stores by remember { mutableStateOf(listOf<String>()) }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    placeholder = { Text("Nombre de la tienda") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Lista de tiendas agregadas
                stores.forEach { store ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = store,
                            fontSize = 18.sp
                        )
                        OutlinedButton(
                            onClick = { /* TODO: Acción del botón + */ },
                            shape = androidx.compose.foundation.shape.CircleShape,
                            border = androidx.compose.material3.ButtonDefaults.outlinedButtonBorder,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            modifier = Modifier
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            stores = stores + text
                            text = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MerkeloRed,
                        contentColor = White100
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.85f)
                        .padding(horizontal = 0.dp)
                ) {
                    Text("Guardar", color = White100, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: Acción de visualizar en el mapa */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MerkeloRed,
                        contentColor = White100
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.85f)
                        .padding(horizontal = 0.dp)
                ) {
                    Text("Visualizar en el mapa", color = White100, fontSize = 18.sp)
                }
                // Contenido futuro de Tiendas
            }
        }
    }
}
