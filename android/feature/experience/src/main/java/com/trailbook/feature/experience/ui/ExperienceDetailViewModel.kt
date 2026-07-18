package com.trailbook.feature.experience.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trailbook.core.common.Result
import com.trailbook.core.common.UiState
import com.trailbook.core.network.model.CommentDto
import com.trailbook.core.network.model.ExperienceDetailDto
import com.trailbook.feature.experience.data.ExperienceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperienceDetailViewModel @Inject constructor(
    private val repository: ExperienceRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val experienceId: String = checkNotNull(savedStateHandle["experienceId"])

    private val _state = MutableStateFlow<UiState<ExperienceDetailDto>>(UiState.Loading)
    val state: StateFlow<UiState<ExperienceDetailDto>> = _state.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentDto>>(emptyList())
    val comments: StateFlow<List<CommentDto>> = _comments.asStateFlow()

    init {
        load()
        loadComments()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (val result = repository.getById(experienceId)) {
                is Result.Success -> _state.value = UiState.Success(result.data)
                is Result.Error -> _state.value = UiState.Error(result.message)
            }
        }
    }

    fun loadComments() {
        viewModelScope.launch {
            when (val result = repository.getComments(experienceId)) {
                is Result.Success -> _comments.value = result.data.content
                is Result.Error -> Unit
            }
        }
    }

    fun toggleLike() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            if (current.likedByCurrentUser) repository.unlike(experienceId) else repository.like(experienceId)
            load()
        }
    }

    fun toggleBookmark() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            if (current.bookmarkedByCurrentUser) repository.removeBookmark(experienceId)
            else repository.bookmark(experienceId)
            load()
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            when (repository.addComment(experienceId, content)) {
                is Result.Success -> loadComments()
                is Result.Error -> Unit
            }
        }
    }

    fun shareExperience() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, current.title)
            putExtra(
                Intent.EXTRA_TEXT,
                "${current.title}\n${current.destination ?: ""}\n\n${current.overview ?: ""}"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share experience").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
