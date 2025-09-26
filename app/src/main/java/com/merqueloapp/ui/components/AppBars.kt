package com.merqueloapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqueloapp.R
import com.merqueloapp.navigation.Routes

@Composable
fun AppTopBar(title: String) {
    Surface(shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    onSelect: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { onSelect(Routes.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.MARKET,
            onClick = { onSelect(Routes.MARKET) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Market") },
            label = { Text("Market") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.STORES,
            onClick = { onSelect(Routes.STORES) },
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Tiendas") },
            label = { Text("Tiendas") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.PROFILE,
            onClick = { onSelect(Routes.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
