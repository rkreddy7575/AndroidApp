package com.trailbook.core.network.interceptor

import com.trailbook.core.datastore.TokenStorage
import com.trailbook.core.network.api.AuthApi
import com.trailbook.core.network.model.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        return runBlocking {
            val refreshToken = tokenStorage.getRefreshToken() ?: return@runBlocking null
            try {
                val apiResponse = authApiProvider.get().refresh(RefreshRequestDto(refreshToken))
                val data = apiResponse.data ?: return@runBlocking null
                tokenStorage.saveTokens(data.accessToken, data.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${data.accessToken}")
                    .build()
            } catch (_: Exception) {
                tokenStorage.clearTokens()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
