package com.trailbook.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.trailbook.feature.home.data.ExperienceFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: ExperienceFeedRepository
) : ViewModel() {
    val feed = repository.getFeed().cachedIn(viewModelScope)
}
