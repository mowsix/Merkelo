package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import com.merqueloapp.data.local.MarketListEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app)

    val allLists: StateFlow<List<MarketListEntity>> =
        repo.listsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteStores: StateFlow<List<String>> =
        repo.favoritesFlow()
            .map { it.map { fs -> fs.name } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteList(id: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteList(id)
            onDone()
        }
    }

    fun deleteFavoriteStore(name: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteFavoriteStore(name)
            onDone()
        }
    }
}
