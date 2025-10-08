package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MarketRepository(app)

    private val _detail = MutableStateFlow<MarketRepository.ListDetail?>(null)
    val detail: StateFlow<MarketRepository.ListDetail?> = _detail

    private var currentListId: Long = -1L

    fun load(listId: Long) {
        currentListId = listId
        viewModelScope.launch {
            _detail.value = repo.getListDetail(listId)
        }
    }

    fun removeStore(storeName: String) {
        if (currentListId <= 0) return
        viewModelScope.launch {
            repo.removeStoreFromList(currentListId, storeName)
            _detail.value = repo.getListDetail(currentListId) // refrescar
        }
    }

    fun removeProduct(storeName: String, productName: String) {
        if (currentListId <= 0) return
        viewModelScope.launch {
            repo.removeProductFromList(currentListId, storeName, productName)
            _detail.value = repo.getListDetail(currentListId) // refrescar
        }
    }
}
