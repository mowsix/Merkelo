package com.merqueloapp.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val MARKET = "market"
    const val STORES = "stores"
    const val PROFILE = "profile"
    const val CREATE_LIST = "create_list"
    const val ADD_PRODUCT = "add_product"

    // Detalle de lista por ID
    const val LIST_DETAIL = "list_detail/{listId}"
    fun listDetailRoute(listId: Long) = "list_detail/$listId"
}

object SplashConfig {
    const val TIME_SPLASH_MS = 900L
}
