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

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app)

    // Todas las listas guardadas
    val allLists: StateFlow<List<MarketListEntity>> =
        repo.listsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Nombres de tiendas favoritas desde Room
    val favoriteStores: StateFlow<List<String>> =
        repo.favoritesFlow()
            .map { list -> list.map { it.name } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
