package com.trailbook.core.network

import com.trailbook.core.common.Result
import com.trailbook.core.network.model.ApiResponse

inline fun <T> ApiResponse<T>.unwrap(): T {
    if (!success || data == null) {
        throw IllegalStateException(message ?: "Request failed")
    }
    return data
}

suspend inline fun <T> safeApiCall(crossinline call: suspend () -> ApiResponse<T>): Result<T> {
    return try {
        val response = call()
        if (response.success && response.data != null) {
            Result.Success(response.data)
        } else {
            Result.Error(response.message ?: "Request failed")
        }
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error", e)
    }
}
