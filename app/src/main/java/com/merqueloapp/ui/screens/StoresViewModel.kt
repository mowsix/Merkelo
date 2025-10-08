package com.merqueloapp.ui.screens


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoresViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app)

    // Tiendas quemadas
    val defaultStores = listOf("D1", "Ara", "Euro", "Éxito", "Carulla", "La Vaquita")

    // Favoritas desde Room (solo nombres)
    val favorites: StateFlow<List<String>> =
        repo.favoritesFlow()
            .map { it.map { e -> e.name } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addFavorite(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) repo.addFavoriteStore(name)
    }

    fun removeFavorite(name: String) = viewModelScope.launch {
        repo.removeFavoriteStore(name)
    }
}
