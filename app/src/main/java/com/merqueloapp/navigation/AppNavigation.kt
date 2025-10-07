package com.merqueloapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.merqueloapp.ui.screens.AddProductScreen
import com.merqueloapp.ui.screens.CreateListScreen
import com.merqueloapp.ui.screens.HomeScreen
import com.merqueloapp.ui.screens.ListDetailScreen
import com.merqueloapp.ui.screens.MarketScreen
import com.merqueloapp.ui.screens.ProfileScreen
import com.merqueloapp.ui.screens.SplashScreen
import com.merqueloapp.ui.screens.StoresScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH
        ) {
            // Splash
            composable(Routes.SPLASH) {
                SplashScreen(
                    goToHome = {
                        nav.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Home
            composable(Routes.HOME) {
                HomeScreen(
                    currentRoute = currentRoute,
                    onCreateNew = { nav.navigate(Routes.CREATE_LIST) },
                    onOpenList = { id -> nav.navigate(Routes.listDetailRoute(id)) },
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) }
                )
            }

            // Market
            composable(Routes.MARKET) {
                MarketScreen(
                    currentRoute = currentRoute,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) },
                    onCreateNew = { nav.navigate(Routes.CREATE_LIST) },
                    onAddProduct = { nav.navigate(Routes.addProductRoute()) } // sin preselección
                )
            }

            // Crear lista
            composable(Routes.CREATE_LIST) {
                CreateListScreen(
                    currentRoute = Routes.MARKET,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) }
                )
            }

            // Stores (placeholder)
            composable(Routes.STORES) {
                StoresScreen(
                    currentRoute = currentRoute,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) }
                )
            }

            // Profile -> lápiz navega a AddProduct con listId preseleccionado
            composable(Routes.PROFILE) {
                ProfileScreen(
                    currentRoute = currentRoute,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) },
                    onEditList = { listId: Long ->
                        nav.navigate(Routes.addProductRoute(listId))
                    }
                )
            }

            // Add Product con listId opcional (?listId=)
            composable(
                route = Routes.ADD_PRODUCT_ROUTE,            // e.g. "add_product?listId={listId}"
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStack: NavBackStackEntry ->             // 👈 tipo explícito
                val preId = backStack.arguments?.getLong("listId") ?: -1L
                val preselect = preId.takeIf { it != -1L }

                AddProductScreen(
                    currentRoute = Routes.MARKET,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) },
                    preselectListId = preselect
                )
            }

            // Detalle de lista por ID (segment param)
            composable(
                route = Routes.LIST_DETAIL,                  // "list_detail/{listId}"
                arguments = listOf(navArgument("listId") { type = NavType.LongType })
            ) { backStack: NavBackStackEntry ->             // 👈 tipo explícito
                val listId = backStack.arguments?.getLong("listId") ?: 0L
                ListDetailScreen(
                    listId = listId,
                    currentRoute = Routes.HOME,
                    onSelectTab = { route -> navigateSingleTopTo(route, nav) }
                )
            }
        }
    }
}

private fun navigateSingleTopTo(navRoute: String, navController: NavController) {
    navController.navigate(navRoute) {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.startDestinationId) { saveState = true }
    }
}
