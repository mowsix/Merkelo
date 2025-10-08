package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    // lista de tiendas de la lista en construcción
    private val _stores = MutableStateFlow(mutableListOf<StoreSelection>())
    val stores: StateFlow<List<StoreSelection>> = _stores

    private val _storeSuggestions = MutableStateFlow<List<String>>(emptyList())
    val storeSuggestions: StateFlow<List<String>> = _storeSuggestions

    private val _productSuggestions = MutableStateFlow<List<String>>(emptyList())
    val productSuggestions: StateFlow<List<String>> = _productSuggestions

    fun setListName(s: String) { _listName.value = s }

    fun loadSuggestions() {
        viewModelScope.launch {
            // defaults + favoritas + usadas
            _storeSuggestions.value = repo.storeSuggestions()
            _productSuggestions.value = repo.productSuggestions()
        }
    }

    /** Agrega la tienda si no existe (NO sobreescribe). Devuelve el nombre normalizado. */
    fun addStore(name: String): String {
        val s = name.toTitleCase()
        if (_stores.value.none { it.name.equals(s, true) }) {
            _stores.value.add(StoreSelection(s))
            _stores.value = _stores.value.toMutableList() // trigger recomposition
        }
        return s
    }

    fun removeStore(name: String) {
        val n = name.toTitleCase()
        _stores.value.removeAll { it.name.equals(n, true) }
        _stores.value = _stores.value.toMutableList()
    }

    /** Reemplaza completamente los productos de una tienda. Crea la tienda si no existe. */
    fun setProductsForStore(storeName: String, products: List<ProductSelection>) {
        val s = storeName.toTitleCase()
        val normalized = products
            .map { ProductSelection(it.name.toTitleCase(), it.quantity.coerceAtLeast(1)) }
            .toMutableList()

        val list = _stores.value.toMutableList()
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        if (idx >= 0) {
            list[idx] = list[idx].copy(products = normalized)
        } else {
            list.add(StoreSelection(name = s, products = normalized))
        }
        _stores.value = list
    }

    /** Agrega o actualiza un producto puntual en una tienda. */
    fun addProduct(storeName: String, prod: String, qty: Int) {
        val s = storeName.toTitleCase()
        val p = prod.toTitleCase()
        val list = _stores.value.toMutableList()
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        val store = if (idx >= 0) list[idx] else StoreSelection(s).also { list.add(it) }

        val existing = store.products.find { it.name.equals(p, true) }
        if (existing == null) {
            store.products.add(ProductSelection(p, qty.coerceAtLeast(1)))
        } else {
            existing.quantity = qty.coerceAtLeast(1)
        }
        _stores.value = list
    }

    fun removeProduct(storeName: String, prod: String) {
        val s = storeName.toTitleCase()
        val p = prod.toTitleCase()
        val list = _stores.value.toMutableList()
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        if (idx >= 0) {
            val st = list[idx]
            st.products.removeAll { it.name.equals(p, true) }
            _stores.value = list
        }
    }

    fun save(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val usableStores = _stores.value
                .filter { it.products.isNotEmpty() } // solo guardamos tiendas con productos
            val map = usableStores.associate { st ->
                st.name to st.products.map { it.name to it.quantity }
            }
            val id = repo.createListWithStoresAndItems(_listName.value.toTitleCase(), map)
            onSaved(id)
        }
    }
}

private fun String.toTitleCase(): String =
    lowercase().split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
