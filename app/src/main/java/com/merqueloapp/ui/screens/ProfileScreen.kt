package com.merqueloapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloDarkRed
import com.merqueloapp.ui.theme.MerkeloRed
import com.merqueloapp.ui.theme.White100

@Composable
fun ProfileScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit
) {
    // 🔹 Datos simulados por ahora (luego pueden venir de una BD o ViewModel)
    var listas by remember { mutableStateOf(listOf("Lista Mercado 1", "Lista Mercado 2")) }
    var supermercados by remember { mutableStateOf(listOf("Tienda 1", "Tienda 2")) }

    Scaffold(
        topBar = { AppTopBar(title = "Perfil") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
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
                // 🔸 Encabezado
                item {
                    Text(
                        text = "MERKELO",
                        color = MerkeloDarkRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 🔸 Sección de listas
                item {
                    SectionTitle("Historial de listas")
                }

                items(listas) { lista ->
                    ProfileCard(
                        title = lista,
                        onEditClick = { /* acción editar lista */ }
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                // 🔸 Sección de supermercados
                item {
                    SectionTitle("Historial de supermercados")
                }

                items(supermercados) { tienda ->
                    ProfileCard(
                        title = tienda,
                        onEditClick = { /* acción editar tienda */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MerkeloDarkRed,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        modifier = Modifier
            .align(Alignment.Start)
            .padding(bottom = 8.dp)
    )
}

@Composable
fun ProfileCard(
    title: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 16.sp
            )

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MerkeloRed
                )
            }
        }
    }
}

