package com.merqueloapp.data.local

import androidx.room.*

/* =========================
 * ENTITIES
 * ========================= */

@Entity(tableName = "market_lists")
data class MarketListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "list_stores",
    foreignKeys = [
        ForeignKey(
            entity = MarketListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class ListStoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val storeName: String
)

@Entity(
    tableName = "list_items",
    foreignKeys = [
        ForeignKey(
            entity = MarketListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ListStoreEntity::class,
            parentColumns = ["id"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId"), Index("storeId")]
)
data class ListItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val storeId: Long,
    val productName: String,
    val quantity: Int = 1
)

/* tabla de tiendas favoritas */
@Entity(
    tableName = "favorite_stores",
    indices = [Index(value = ["name"], unique = true)]
)
data class FavoriteStoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)
