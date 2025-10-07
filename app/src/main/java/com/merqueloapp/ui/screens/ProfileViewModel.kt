package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import com.merqueloapp.data.local.MarketListEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MarketRepository(app)


    val allLists: StateFlow<List<MarketListEntity>> =
        repo.listsFlow() // ya viene ordenado por createdAt DESC
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
