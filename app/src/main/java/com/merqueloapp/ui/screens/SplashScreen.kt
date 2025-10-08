package com.merqueloapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merqueloapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    goToHome: () -> Unit
) {

    var contador by remember { mutableIntStateOf(0) }

    // animaciones en secuencia
    LaunchedEffect(Unit) {
        repeat(3) {
            delay(500)
            contador++
        }
        goToHome.invoke()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = when (contador) {
                0 -> painterResource(id = R.drawable.cart)
                1 -> painterResource(id = R.drawable.map)
                else -> painterResource(id = R.drawable.store)
            },
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
    }
}
