package com.trailbook.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.trailbook.core.common.Result
import com.trailbook.core.common.UiState
import com.trailbook.core.datastore.TokenStorage
import com.trailbook.core.network.model.ExperienceSummaryDto
import com.trailbook.core.network.model.UserDto
import com.trailbook.feature.authentication.data.AuthRepository
import com.trailbook.feature.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserDto>>(UiState.Loading)
    val userState: StateFlow<UiState<UserDto>> = _userState.asStateFlow()

    private val userId = MutableStateFlow<String?>(null)

    val themeMode = tokenStorage.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val bookmarks = profileRepository.getBookmarks().cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val myExperiences = userId.flatMapLatest { id ->
        if (id == null) {
            flowOf(PagingData.empty())
        } else {
            profileRepository.getMyExperiences(id)
        }
    }.cachedIn(viewModelScope)

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _userState.value = UiState.Loading
            when (val result = profileRepository.getMe()) {
                is Result.Success -> {
                    userId.value = result.data.id
                    _userState.value = UiState.Success(result.data)
                }
                is Result.Error -> _userState.value = UiState.Error(result.message)
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            userId.value = null
            onLoggedOut()
        }
    }

    fun setTheme(mode: String) {
        viewModelScope.launch {
            tokenStorage.setThemeMode(mode)
        }
    }
}
