package com.trailbook.feature.experience.data

import com.trailbook.core.common.Result
import com.trailbook.core.network.api.ExperienceApi
import com.trailbook.core.network.api.MediaApi
import com.trailbook.core.network.model.*
import com.trailbook.core.network.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceRepository @Inject constructor(
    private val experienceApi: ExperienceApi,
    private val mediaApi: MediaApi,
    private val okHttpClient: OkHttpClient
) {
    suspend fun getById(id: String): Result<ExperienceDetailDto> =
        safeApiCall { experienceApi.getById(id) }

    suspend fun create(request: CreateExperienceRequestDto): Result<ExperienceDetailDto> =
        safeApiCall { experienceApi.create(request) }

    suspend fun update(id: String, request: CreateExperienceRequestDto): Result<ExperienceDetailDto> =
        safeApiCall { experienceApi.update(id, request) }

    suspend fun publish(id: String): Result<ExperienceDetailDto> =
        safeApiCall { experienceApi.publish(id) }

    suspend fun like(id: String): Result<Unit> =
        when (val r = safeApiCall { experienceApi.like(id) }) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> r
        }

    suspend fun unlike(id: String): Result<Unit> =
        when (val r = safeApiCall { experienceApi.unlike(id) }) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> r
        }

    suspend fun bookmark(id: String): Result<Unit> =
        when (val r = safeApiCall { experienceApi.bookmark(id) }) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> r
        }

    suspend fun removeBookmark(id: String): Result<Unit> =
        when (val r = safeApiCall { experienceApi.removeBookmark(id) }) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> r
        }

    suspend fun getComments(id: String, page: Int = 0): Result<PageResponse<CommentDto>> =
        safeApiCall { experienceApi.getComments(id, page, 20) }

    suspend fun addComment(id: String, content: String): Result<CommentDto> =
        safeApiCall { experienceApi.addComment(id, CreateCommentRequestDto(content)) }

    suspend fun uploadImage(bytes: ByteArray, fileName: String): Result<String> {
        return try {
            val signatureResponse = mediaApi.getCloudinarySignature()
            val sig = signatureResponse.data
                ?: return Result.Error(signatureResponse.message ?: "Failed to get upload signature")

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), bytes))
                .addFormDataPart("api_key", sig.apiKey)
                .addFormDataPart("timestamp", sig.timestamp.toString())
                .addFormDataPart("signature", sig.signature)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/${sig.cloudName}/image/upload")
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody == null) {
                return Result.Error("Upload failed")
            }
            val url = Regex("\"secure_url\"\\s*:\\s*\"([^\"]+)\"").find(responseBody)?.groupValues?.get(1)
                ?: return Result.Error("Invalid upload response")
            Result.Success(url)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Upload failed", e)
        }
    }
}
