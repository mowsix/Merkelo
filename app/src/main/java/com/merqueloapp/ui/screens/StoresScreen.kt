package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
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
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed
import com.merqueloapp.ui.theme.White100
import androidx.compose.ui.graphics.Color

@Composable
fun StoresScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { AppTopBar(title = "Tiendas") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Box(
            modifier = Modifier.fillMaxSize().padding(inner),
        ) {
            var text by remember { mutableStateOf("") }
            var stores by remember { mutableStateOf(listOf<String>()) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, bottom = 0.dp)
            ) {
                Text("Agrega tus tiendas favoritas")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    placeholder = { Text("Nombre de la tienda") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            brush = SolidColor(Color.LightGray),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        stores.forEach { store ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
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
                                    modifier = Modifier.size(36.dp)
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
                    enabled = text.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (text.isNotBlank()) MerkeloRed else Color(0xFFD3D3D3),
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
                    onClick = {
                        onSelectTab(com.merqueloapp.navigation.Routes.MAP)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MerkeloRed,
                        contentColor = White100
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.85f)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Visualizar en el mapa", color = White100, fontSize = 18.sp)
                }
            }
        }
    }
}
