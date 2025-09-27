package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sugerencias "quemadas" para tiendas
private val defaultStores = listOf("D1", "Euro", "La Vaquita", "Éxito", "Carulla", "Ara", "Isimo", "Surtimax", "Jumbo", "PriceSmart", "Makro")

data class StoreSelection(
    val name: String,
    val products: MutableList<ProductSelection> = mutableListOf()
)

data class ProductSelection(
    val name: String,
    var quantity: Int = 1
)

class CreateListViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app)

    private val _listName = MutableStateFlow("")
    val listName: StateFlow<String> = _listName

    // Lista actual en edición: por ahora 1 sola tienda
    private val _stores = MutableStateFlow(mutableListOf<StoreSelection>())
    val stores: StateFlow<List<StoreSelection>> = _stores

    private val _storeSuggestions = MutableStateFlow<List<String>>(emptyList())
    val storeSuggestions: StateFlow<List<String>> = _storeSuggestions

    private val _productSuggestions = MutableStateFlow<List<String>>(emptyList())
    val productSuggestions: StateFlow<List<String>> = _productSuggestions

    /* -------------------  Estado básico  ------------------- */

    fun setListName(s: String) { _listName.value = s }

    /** Selecciona UNA sola tienda (reemplaza la actual). */
    fun setSingleStore(name: String) {
        val s = name.toTitleCase()
        _stores.value = mutableListOf(StoreSelection(s))
        // Forzar notificación
        _stores.value = _stores.value.toMutableList()
    }

    /** Limpia la tienda seleccionada. */
    fun clearStore() {
        _stores.value.clear()
        _stores.value = _stores.value.toMutableList()
    }

    /* -------------------  Productos  ------------------- */

    /** Agrega o actualiza un producto para la tienda dada. */
    fun addProduct(storeName: String, prod: String, qty: Int) {
        val s = storeName.toTitleCase()
        val p = prod.toTitleCase()
        val list = _stores.value.toMutableList()
        val store = list.find { it.name.equals(s, true) } ?: return
        val existing = store.products.find { it.name.equals(p, true) }
        if (existing == null) {
            store.products.add(ProductSelection(p, qty.coerceAtLeast(1)))
        } else {
            existing.quantity = qty.coerceAtLeast(1)
        }
        _stores.value = list
    }

    /** Quita un producto de la tienda dada. */
    fun removeProduct(storeName: String, prod: String) {
        val list = _stores.value.toMutableList()
        list.find { it.name.equals(storeName, true) }?.let { st ->
            st.products.removeAll { it.name.equals(prod, true) }
            _stores.value = list
        }
    }

    /** Reemplaza COMPLETAMENTE los productos de la tienda (commit desde el diálogo). */
    fun setProductsForStore(storeName: String, products: List<ProductSelection>) {
        val s = storeName.toTitleCase()
        val normalized = products.map {
            ProductSelection(it.name.toTitleCase(), it.quantity.coerceAtLeast(1))
        }
        val list = _stores.value.toMutableList()
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        if (idx >= 0) {
            list[idx] = list[idx].copy(products = normalized.toMutableList())
        } else {
            list.add(StoreSelection(name = s, products = normalized.toMutableList()))
        }
        _stores.value = list
    }

    /* -------------------  Sugerencias  ------------------- */

    fun loadSuggestions() {
        viewModelScope.launch {
            val fromDbStores = repo.storeSuggestions()
            _storeSuggestions.value = (defaultStores + fromDbStores)
                .distinctBy { it.lowercase() }
                .sorted()

            // Por ahora solo desde BD; si quieres defaults de productos,
            // aquí puedes hacer un merge similar.
            _productSuggestions.value = repo.productSuggestions()
        }
    }

    /* -------------------  Guardado  ------------------- */

    fun save(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val map = _stores.value.associate { st ->
                st.name to st.products.map { it.name to it.quantity }
            }
            val id = repo.createListWithStoresAndItems(_listName.value.toTitleCase(), map)
            onSaved(id)
        }
    }
}

/* -------------------  Utils  ------------------- */

private fun String.toTitleCase(): String =
    lowercase().split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
