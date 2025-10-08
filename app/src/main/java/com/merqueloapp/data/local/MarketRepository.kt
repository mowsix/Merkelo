package com.merqueloapp.data

import android.content.Context
import com.merqueloapp.data.local.DbProvider
import com.merqueloapp.data.local.FavoriteStoreEntity
import com.merqueloapp.data.local.ListItemEntity
import com.merqueloapp.data.local.ListStoreEntity
import com.merqueloapp.data.local.MarketListEntity

class MarketRepository(context: Context) {
    private val db = DbProvider.get(context)
    private val listDao = db.marketListDao()
    private val storeDao = db.listStoreDao()
    private val itemDao = db.listItemDao()
    private val favoriteDao = db.favoriteStoreDao()
    /* ---------------- Flujos / Lecturas ---------------- */

    /** Flujo en vivo de listas para la pantalla Home. */
    fun listsFlow() = listDao.observeLists()

    /** Lectura puntual de todas las listas (para pickers, etc.). */
    suspend fun getLists(): List<MarketListEntity> = listDao.getLists()

    /** Nombres de tiendas para una lista dada (para sugerencias). */
    suspend fun storeNamesForList(listId: Long): List<String> =
        storeDao.getStoresForList(listId).map { it.storeName }

    /* ---------------- Creación de lista completa ---------------- */

    /**
     * Crea una lista con sus tiendas y productos.
     * @param storesWithProducts Mapa: "NombreTienda" -> Lista de (Producto, Cantidad)
     */
    suspend fun createListWithStoresAndItems(
        listName: String,
        storesWithProducts: Map<String, List<Pair<String, Int>>>
    ): Long {
        val listId = listDao.insertList(MarketListEntity(name = listName))
        storesWithProducts.forEach { (storeRaw, products) ->
            val storeName = storeRaw.toTitleCase()
            val storeId = storeDao.insertStore(
                ListStoreEntity(listId = listId, storeName = storeName)
            )
            products.forEach { (prodRaw, qty) ->
                val productName = prodRaw.toTitleCase()
                itemDao.insertItem(
                    ListItemEntity(
                        listId = listId,
                        storeId = storeId,
                        productName = productName,
                        quantity = qty.coerceAtLeast(1)
                    )
                )
            }
        }
        return listId
    }

    /* ---------------- Agregar productos a lista existente ---------------- */

    /**
     * Agrega productos a una lista existente. Si la tienda no existe en la lista, la crea.
     * @param listId id de la lista destino
     * @param storeNameRaw nombre de la tienda (se normaliza a Title Case)
     * @param products lista de (producto, cantidad)
     */
    suspend fun addProductsToList(
        listId: Long,
        storeNameRaw: String,
        products: List<Pair<String, Int>>
    ) {
        val storeName = storeNameRaw.toTitleCase()

        // Buscar si la tienda ya existe en la lista (ignore case)
        val existing = storeDao.getStoresForList(listId).firstOrNull {
            it.storeName.equals(storeName, ignoreCase = true)
        }

        val storeId: Long = existing?.id ?: storeDao.insertStore(
            ListStoreEntity(listId = listId, storeName = storeName)
        )

        products.forEach { (pRaw, q) ->
            val productName = pRaw.toTitleCase()
            itemDao.insertItem(
                ListItemEntity(
                    listId = listId,
                    storeId = storeId,
                    productName = productName,
                    quantity = q.coerceAtLeast(1)
                )
            )
        }
    }

    // Devuelve la lista (nombre) + tiendas con sus items
    suspend fun getListDetail(listId: Long): ListDetail {
        val list = listDao.getById(listId)
        val stores = storeDao.getStoresForList(listId)
        val groups = stores.map { st ->
            val items = itemDao.getItemsForStore(st.id)
                .map { ProductEntry(it.productName, it.quantity) }
            StoreGroup(storeName = st.storeName, items = items)
        }
        return ListDetail(listId = list.id, listName = list.name, groups = groups)
    }

    // ----- FAVORITAS -----
    fun favoritesFlow() = favoriteDao.observeFavorites()
    suspend fun favoriteStores(): List<String> = favoriteDao.getFavoriteNames()

    suspend fun addFavoriteStore(name: String) {
        favoriteDao.insert(FavoriteStoreEntity(name = name.toTitleCase()))
    }
    suspend fun removeFavoriteStore(name: String) {
        favoriteDao.deleteByName(name.toTitleCase())
    }



    // Modelos para la UI
    data class ListDetail(
        val listId: Long,
        val listName: String,
        val groups: List<StoreGroup>
    )
    data class StoreGroup(
        val storeName: String,
        val items: List<ProductEntry>
    )
    data class ProductEntry(
        val name: String,
        val quantity: Int
    )
    /* ---------------- Sugerencias globales ---------------- */


    // ----- SUGERENCIAS DE TIENDAS (defaults + favoritas + usadas) -----
    suspend fun storeSuggestions(): List<String> {
        val defaults = listOf("D1", "Ara", "Euro", "Éxito", "Carulla", "La Vaquita")
        val favs = favoriteStores()
        val used = storeDao.getStoreSuggestions()
        return (defaults + favs + used)
            .distinctBy { it.lowercase() }
            .sorted()
    }

    suspend fun productSuggestions(): List<String> = itemDao.getProductSuggestions()

    //tienda completa (items se borran por CASCADE)
    suspend fun removeStoreFromList(listId: Long, storeName: String) {
        storeDao.deleteStoreByName(listId, storeName)
    }

    //un producto de una tienda
    suspend fun removeProductFromList(listId: Long, storeName: String, productName: String) {
        val store = storeDao.getStoreByName(listId, storeName) ?: return
        itemDao.deleteItemByName(listId, store.id, productName)
    }

    suspend fun deleteList(listId: Long) {
        listDao.deleteList(listId)
    }

    // 👇 borrar tienda favorita
    suspend fun deleteFavoriteStore(name: String) {
        favoriteDao.deleteByName(name)
    }
}



/* ---------------- Utils ---------------- */

private fun String.toTitleCase(): String =
    lowercase().split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
