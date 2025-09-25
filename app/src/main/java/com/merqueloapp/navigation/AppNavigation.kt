package com.merqueloapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.merqueloapp.ui.screens.HomeScreen
import com.merqueloapp.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    goToHome = {
                        nav.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) { HomeScreen(
                onCreateNew = { /* TODO: abrir pantalla crear lista */ },
                onOpenList = { /* TODO: abrir detalle lista */ },
                onTabSelected = { route ->
                    // Por ahora sólo HOME está implementado
                    if (route != Routes.HOME) {
                        // TODO: navega a CART/STORES/PROFILE cuando existan
                    }
                }
            ) }
        }
    }
}
