package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import com.merqueloapp.data.local.MarketListEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val defaultStores = listOf("D1", "Euro", "La Vaquita", "Éxito", "Carulla", "Ara")

class AddProductViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app)

    private val _lists = MutableStateFlow<List<MarketListEntity>>(emptyList())
    val lists: StateFlow<List<MarketListEntity>> = _lists

    private val _selectedList = MutableStateFlow<MarketListEntity?>(null)
    val selectedList: StateFlow<MarketListEntity?> = _selectedList

    private val _storeName = MutableStateFlow<String?>(null)
    val storeName: StateFlow<String?> = _storeName

    private val _storeSuggestions = MutableStateFlow<List<String>>(emptyList())
    val storeSuggestions: StateFlow<List<String>> = _storeSuggestions

    private val _productSuggestions = MutableStateFlow<List<String>>(emptyList())
    val productSuggestions: StateFlow<List<String>> = _productSuggestions

    private val _products = MutableStateFlow(mutableListOf<ProductSelection>())
    val products: StateFlow<List<ProductSelection>> = _products

    fun loadInitial() {
        viewModelScope.launch {
            _lists.value = repo.getLists()
            _productSuggestions.value = repo.productSuggestions()
        }
    }

    fun selectList(list: MarketListEntity) {
        _selectedList.value = list
        _products.value.clear()
        _storeName.value = null
        viewModelScope.launch {
            val fromDb = repo.storeNamesForList(list.id)
            _storeSuggestions.value = (defaultStores + fromDb)
                .distinctBy { it.lowercase() }
                .sorted()
        }
    }

    fun setStore(name: String) { _storeName.value = name }

    fun setProducts(newProducts: List<ProductSelection>) {
        _products.value = newProducts.toMutableList()
        _products.value = _products.value.toMutableList()
    }

    fun save(onSaved: () -> Unit) {
        val list = _selectedList.value ?: return
        val store = _storeName.value ?: return
        val pts = _products.value.map { it.name to it.quantity }
        viewModelScope.launch {
            repo.addProductsToList(list.id, store, pts)
            onSaved()
        }
    }

    fun preselectListById(listId: Long) {
        viewModelScope.launch {
            if (_lists.value.isEmpty()) {
                _lists.value = repo.getLists()
            }
            _lists.value.firstOrNull { it.id == listId }?.let { selectList(it) }
        }
    }
}
