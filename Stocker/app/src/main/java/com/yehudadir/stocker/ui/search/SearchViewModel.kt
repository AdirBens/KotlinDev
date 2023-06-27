package com.yehudadir.stocker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.yehudadir.common.Resource
import com.yehudadir.stocker.data.model.SymbolSearch
import com.yehudadir.stocker.data.repository.StockRepository
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