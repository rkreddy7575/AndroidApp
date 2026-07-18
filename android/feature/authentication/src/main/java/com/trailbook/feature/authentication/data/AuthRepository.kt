package com.trailbook.feature.authentication.data

import com.trailbook.core.common.Result
import com.trailbook.core.datastore.TokenStorage
import com.trailbook.core.network.api.AuthApi
import com.trailbook.core.network.model.LoginRequestDto
import com.trailbook.core.network.model.RegisterRequestDto
import com.trailbook.core.network.model.UserDto
import com.trailbook.core.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    val isLoggedIn = tokenStorage.isLoggedIn

    suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): Result<UserDto> {
        return when (val result = safeApiCall {
            authApi.register(RegisterRequestDto(username, email, password, displayName))
        }) {
            is Result.Success -> {
                tokenStorage.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(result.data.user)
            }
            is Result.Error -> result
        }
    }

    suspend fun login(email: String, password: String): Result<UserDto> {
        return when (val result = safeApiCall {
            authApi.login(LoginRequestDto(email, password))
        }) {
            is Result.Success -> {
                tokenStorage.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(result.data.user)
            }
            is Result.Error -> result
        }
    }

    suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception) {
        }
        tokenStorage.clearTokens()
    }
}
