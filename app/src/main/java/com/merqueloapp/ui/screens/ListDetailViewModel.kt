package com.merqueloapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merqueloapp.data.MarketRepository
import com.merqueloapp.data.MarketRepository.ListDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MarketRepository(app)

    private val _detail = MutableStateFlow<ListDetail?>(null)
    val detail: StateFlow<ListDetail?> = _detail

    fun load(listId: Long) {
        viewModelScope.launch {
            _detail.value = repo.getListDetail(listId)
        }
    }
}
