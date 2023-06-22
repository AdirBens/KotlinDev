package com.example.stocker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.stocker.utils.Resource
import com.example.stocker.data.model.SymbolSearch
import com.example.stocker.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _keyword = MutableLiveData<String>()

    private val _searchResults = _keyword.switchMap {
        stockRepository.getSymbolSearch(it)
    }
    val searchResults : LiveData<Resource<SymbolSearch>> = _searchResults

    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }
}