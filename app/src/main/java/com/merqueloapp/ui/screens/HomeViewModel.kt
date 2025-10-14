package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import com.merqueloapp.data.local.MarketListEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para la pantalla principal que maneja la logica de negocio
 * relacionada con las listas de mercado
 */
class HomeViewModel(app: Application) : AndroidViewModel(app) {
    // repositorio para acceder a los datos de las listas
    private val repo = MarketRepository(app)

    // flujo de estado que contiene la lista de mercados
    // se actualiza automaticamente cuando hay cambios en la base de datos
    val lists: StateFlow<List<MarketListEntity>> =
        repo.listsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000), // mantiene activo por 5 segundos despues de perder suscriptores
                initialValue = emptyList()
            )
}
