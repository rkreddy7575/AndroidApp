package com.trailbook.feature.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.trailbook.feature.explore.data.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: ExploreRepository
) : ViewModel() {

    private val searchTrigger = MutableStateFlow(SearchParams(null, "recent"))
    private val _sort = MutableStateFlow("recent")
    val sort: StateFlow<String> = _sort.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val results = searchTrigger
        .flatMapLatest { params ->
            repository.search(params.query, params.destination, params.sort)
        }
        .cachedIn(viewModelScope)

    fun updateQuery(query: String) {
        searchTrigger.value = searchTrigger.value.copy(query = query.ifBlank { null })
    }

    fun updateSort(sort: String) {
        _sort.value = sort
        searchTrigger.value = searchTrigger.value.copy(sort = sort)
    }

    private data class SearchParams(
        val query: String?,
        val sort: String,
        val destination: String? = null
    )
}
