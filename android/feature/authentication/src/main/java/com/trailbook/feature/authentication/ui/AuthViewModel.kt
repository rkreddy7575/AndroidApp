package com.trailbook.feature.authentication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trailbook.core.common.Result
import com.trailbook.core.common.UiState
import com.trailbook.core.network.model.UserDto
import com.trailbook.feature.authentication.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _authState = MutableStateFlow<UiState<UserDto>?>(null)
    val authState: StateFlow<UiState<UserDto>?> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _authState.value = UiState.Success(result.data)
                is Result.Error -> _authState.value = UiState.Error(result.message)
            }
        }
    }

    fun register(username: String, email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val result = authRepository.register(username, email, password, displayName)) {
                is Result.Success -> _authState.value = UiState.Success(result.data)
                is Result.Error -> _authState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _authState.value = null
    }
}
