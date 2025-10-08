package com.merqueloapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/* =========================
 * DAOs
 * ========================= */

@Dao
interface MarketListDao {
    @Insert
    suspend fun insertList(list: MarketListEntity): Long

    @Query("SELECT * FROM market_lists ORDER BY createdAt DESC")
    suspend fun getLists(): List<MarketListEntity>

    @Query("SELECT * FROM market_lists ORDER BY createdAt DESC")
    fun observeLists(): Flow<List<MarketListEntity>>

    @Query("SELECT * FROM market_lists WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MarketListEntity
}

@Dao
interface ListStoreDao {
    @Insert
    suspend fun insertStore(store: ListStoreEntity): Long

    @Query("SELECT DISTINCT storeName FROM list_stores ORDER BY storeName ASC")
    suspend fun getStoreSuggestions(): List<String>

    @Query("SELECT * FROM list_stores WHERE listId = :listId")
    suspend fun getStoresForList(listId: Long): List<ListStoreEntity>
}

@Dao
interface ListItemDao {
    @Insert
    suspend fun insertItem(item: ListItemEntity): Long

    @Query("SELECT DISTINCT productName FROM list_items ORDER BY productName ASC")
    suspend fun getProductSuggestions(): List<String>

    @Query("SELECT * FROM list_items WHERE listId = :listId AND storeId = :storeId")
    suspend fun getItemsForStore(listId: Long, storeId: Long): List<ListItemEntity>

    @Query("SELECT * FROM list_items WHERE storeId = :storeId ORDER BY id")
    suspend fun getItemsForStore(storeId: Long): List<ListItemEntity>
}

/** favoritas */
@Dao
interface FavoriteStoreDao {

    @Query("SELECT * FROM favorite_stores ORDER BY name")
    fun observeFavorites(): Flow<List<FavoriteStoreEntity>>

    @Query("SELECT name FROM favorite_stores ORDER BY name")
    suspend fun getFavoriteNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoriteStoreEntity): Long

    @Query("DELETE FROM favorite_stores WHERE name = :name")
    suspend fun deleteByName(name: String)
}
