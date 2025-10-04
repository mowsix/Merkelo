package com.merqueloapp.data

import android.content.Context
import com.merqueloapp.data.local.DbProvider
import com.merqueloapp.data.local.ListItemEntity
import com.merqueloapp.data.local.ListStoreEntity
import com.merqueloapp.data.local.MarketListEntity

class MarketRepository(context: Context) {
    private val db = DbProvider.get(context)
    private val listDao = db.marketListDao()
    private val storeDao = db.listStoreDao()
    private val itemDao = db.listItemDao()

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
        val list = listDao.getById(listId)              // 👈 requiere DAO getById
        val stores = storeDao.getStoresForList(listId)
        val groups = stores.map { st ->
            val items = itemDao.getItemsForStore(st.id) // 👈 requiere DAO getItemsForStore
                .map { ProductEntry(it.productName, it.quantity) }
            StoreGroup(storeName = st.storeName, items = items)
        }
        return ListDetail(listId = list.id, listName = list.name, groups = groups)
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

    suspend fun storeSuggestions(): List<String> = storeDao.getStoreSuggestions()

    suspend fun productSuggestions(): List<String> = itemDao.getProductSuggestions()
}

/* ---------------- Utils ---------------- */

private fun String.toTitleCase(): String =
    lowercase().split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
