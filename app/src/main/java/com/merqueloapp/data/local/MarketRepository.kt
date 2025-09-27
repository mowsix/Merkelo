package com.merqueloapp.data

import android.content.Context
import com.merqueloapp.data.local.*

class MarketRepository(context: Context) {
    private val db = DbProvider.get(context)
    private val listDao = db.marketListDao()
    private val storeDao = db.listStoreDao()
    private val itemDao = db.listItemDao()

    fun listsFlow() = listDao.observeLists()

    suspend fun createListWithStoresAndItems(
        listName: String,
        storesWithProducts: Map<String, List<Pair<String, Int>>>
    ): Long {
        val listId = listDao.insertList(MarketListEntity(name = listName))
        storesWithProducts.forEach { (storeRaw, products) ->
            val storeName = storeRaw.toTitleCase()
            val storeId = storeDao.insertStore(ListStoreEntity(listId = listId, storeName = storeName))
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

    suspend fun storeSuggestions(): List<String> = storeDao.getStoreSuggestions()
    suspend fun productSuggestions(): List<String> = itemDao.getProductSuggestions()
}

private fun String.toTitleCase(): String =
    lowercase().split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
