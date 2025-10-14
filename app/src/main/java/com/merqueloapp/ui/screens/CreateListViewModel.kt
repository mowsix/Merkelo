// viewmodel para la pantalla de creacion de listas
package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Clase que representa una tienda seleccionada en la lista
 * @property name nombre de la tienda
 * @property products lista de productos en la tienda
 */
data class StoreSelection(
    val name: String,
    val products: MutableList<ProductSelection> = mutableListOf()
)

/**
 * Clase que representa un producto seleccionado en una tienda
 * @property name nombre del producto
 * @property quantity cantidad del producto (por defecto 1)
 */
data class ProductSelection(
    val name: String,
    var quantity: Int = 1
)

/**
 * ViewModel para la pantalla de creacion de listas de mercado
 * Maneja el estado y la logica de negocio relacionada con la creacion de listas
 */
class CreateListViewModel(app: Application) : AndroidViewModel(app) {
    // repositorio para acceder a los datos
    private val repo = MarketRepository(app)

    // estado del nombre de la lista
    private val _listName = MutableStateFlow("")
    val listName: StateFlow<String> = _listName

    // estado de la lista de tiendas seleccionadas
    private val _stores = MutableStateFlow(mutableListOf<StoreSelection>())
    val stores: StateFlow<List<StoreSelection>> = _stores

    // sugerencias de nombres de tiendas
    private val _storeSuggestions = MutableStateFlow<List<String>>(emptyList())
    val storeSuggestions: StateFlow<List<String>> = _storeSuggestions

    // sugerencias de nombres de productos
    private val _productSuggestions = MutableStateFlow<List<String>>(emptyList())
    val productSuggestions: StateFlow<List<String>> = _productSuggestions

    /**
     * Establece el nombre de la lista
     * @param s nuevo nombre para la lista
     */
    fun setListName(s: String) { 
        _listName.value = s 
    }

    /**
     * Carga las sugerencias de tiendas y productos desde el repositorio
     * Se ejecuta al iniciar la pantalla
     */
    fun loadSuggestions() {
        viewModelScope.launch {
            // combina tiendas por defecto, favoritas y usadas recientemente
            _storeSuggestions.value = repo.storeSuggestions()
            _productSuggestions.value = repo.productSuggestions()
        }
    }

    /**
     * Agrega una nueva tienda a la lista si no existe
     * @param name nombre de la tienda a agregar
     * @return nombre normalizado de la tienda
     */
    fun addStore(name: String): String {
        val s = name.toTitleCase()
        if (_stores.value.none { it.name.equals(s, true) }) {
            _stores.value.add(StoreSelection(s))
            _stores.value = _stores.value.toMutableList() // fuerza la actualizacion del estado
        }
        return s
    }

    /**
     * Elimina una tienda de la lista
     * @param name nombre de la tienda a eliminar
     */
    fun removeStore(name: String) {
        val n = name.toTitleCase()
        _stores.value.removeAll { it.name.equals(n, true) }
        _stores.value = _stores.value.toMutableList() // fuerza la actualizacion
    }

    /**
     * Establece la lista completa de productos para una tienda especifica
     * Si la tienda no existe, la crea
     * @param storeName nombre de la tienda
     * @param products lista de productos a establecer
     */
    fun setProductsForStore(storeName: String, products: List<ProductSelection>) {
        val s = storeName.toTitleCase()
        val normalized = products
            .map { ProductSelection(it.name.toTitleCase(), it.quantity.coerceAtLeast(1)) }
            .toMutableList()

        val list = _stores.value.toMutableList()
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        if (idx >= 0) {
            // actualiza productos de la tienda existente
            list[idx] = list[idx].copy(products = normalized)
        } else {
            // crea una nueva tienda con los productos
            list.add(StoreSelection(name = s, products = normalized))
        }
        _stores.value = list
    }

    /**
     * Agrega o actualiza un producto en una tienda especifica
     * @param storeName nombre de la tienda
     * @param prod nombre del producto
     * @param qty cantidad del producto
     */
    fun addProduct(storeName: String, prod: String, qty: Int) {
        val s = storeName.toTitleCase()
        val p = prod.toTitleCase()
        val list = _stores.value.toMutableList()
        
        // busca la tienda o la crea si no existe
        val idx = list.indexOfFirst { it.name.equals(s, true) }
        val store = if (idx >= 0) list[idx] else StoreSelection(s).also { list.add(it) }

        // actualiza cantidad si el producto ya existe, de lo contrario lo agrega
        val existing = store.products.find { it.name.equals(p, true) }
        if (existing == null) {
            store.products.add(ProductSelection(p, qty.coerceAtLeast(1)))
        } else {
            existing.quantity = qty.coerceAtLeast(1)
        }
        _stores.value = list
    }

    /**
     * Elimina un producto de una tienda especifica
     * @param storeName nombre de la tienda
     * @param prod nombre del producto a eliminar
     */
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
