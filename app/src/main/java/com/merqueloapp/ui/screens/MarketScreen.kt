package com.merqueloapp.ui.screens

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqueloapp.R
import com.merqueloapp.ui.components.AppBottomBar
import com.merqueloapp.ui.components.AppTopBar
import com.merqueloapp.ui.theme.MerkeloRed

@Composable
fun MarketScreen(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    onCreateNew: () -> Unit,
    onAddProduct: () -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(title = "Mercado") },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Imagen hero (encabezado)
            Image(
                painter = painterResource(id = R.drawable.market),
                contentDescription = "Imagen Mercado",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(70.dp))

            // Título 1
            Text(
                text = "Crea una nueva lista de mercado",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(horizontal = 60.dp).padding(vertical = 20.dp),
                fontWeight = FontWeight.Medium

            )

            // Botón "Crear nuevo"
            RoundedBigButton(
                text = "Crear nuevo",
                onClick = onCreateNew,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                backgroundColor = MerkeloRed,
                contentColor = Color.White
            )

            Spacer(Modifier.height(60.dp))

            // Título 2
            Text(
                text = "Agregar un producto a una lista existente",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(30.dp))

            // Botón "Agregar producto"
            RoundedBigButton(
                text = "Agregar producto",
                onClick = { onAddProduct.invoke() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                backgroundColor = MerkeloRed,
                contentColor = Color.White
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoundedBigButton(
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .height(56.dp).padding(horizontal = 38.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

