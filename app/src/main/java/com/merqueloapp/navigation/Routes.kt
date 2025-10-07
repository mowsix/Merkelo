package com.merqueloapp.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val MARKET = "market"
    const val STORES = "stores"
    const val PROFILE = "profile"
    const val CREATE_LIST = "create_list"

    // Detalle de lista por ID (segment param)
    const val LIST_DETAIL = "list_detail/{listId}"
    fun listDetailRoute(listId: Long) = "list_detail/$listId"

    // Add Product con listId opcional (query param)
    private const val ADD_PRODUCT_BASE = "add_product"
    const val ADD_PRODUCT_ROUTE = "$ADD_PRODUCT_BASE?listId={listId}" // patrón para el NavHost

    /** Construye la ruta real a navegar. Si listId es null, navega sin query param. */
    fun addProductRoute(listId: Long? = null): String =
        if (listId != null) "$ADD_PRODUCT_BASE?listId=$listId" else ADD_PRODUCT_BASE



}

object SplashConfig {
    const val TIME_SPLASH_MS = 900L
}

