package com.merqueloapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MarketListEntity::class, ListStoreEntity::class, ListItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun marketListDao(): MarketListDao
    abstract fun listStoreDao(): ListStoreDao
    abstract fun listItemDao(): ListItemDao
}
