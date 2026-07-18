package com.trailbook.core.network.api

import com.trailbook.core.network.model.*
import retrofit2.http.*

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ApiResponse<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponse<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): ApiResponse<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit?>
}

interface ExperienceApi {
    @GET("experiences/feed")
    suspend fun getFeed(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<ExperienceSummaryDto>>

    @GET("experiences/search")
    suspend fun search(
        @Query("q") query: String?,
        @Query("destination") destination: String?,
        @Query("sort") sort: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<ExperienceSummaryDto>>

    @GET("experiences/{id}")
    suspend fun getById(@Path("id") id: String): ApiResponse<ExperienceDetailDto>

    @POST("experiences")
    suspend fun create(@Body request: CreateExperienceRequestDto): ApiResponse<ExperienceDetailDto>

    @PUT("experiences/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body request: CreateExperienceRequestDto
    ): ApiResponse<ExperienceDetailDto>

    @POST("experiences/{id}/publish")
    suspend fun publish(@Path("id") id: String): ApiResponse<ExperienceDetailDto>

    @DELETE("experiences/{id}")
    suspend fun delete(@Path("id") id: String): ApiResponse<Unit?>

    @POST("experiences/{id}/like")
    suspend fun like(@Path("id") id: String): ApiResponse<Unit?>

    @DELETE("experiences/{id}/like")
    suspend fun unlike(@Path("id") id: String): ApiResponse<Unit?>

    @POST("experiences/{id}/bookmark")
    suspend fun bookmark(@Path("id") id: String): ApiResponse<Unit?>

    @DELETE("experiences/{id}/bookmark")
    suspend fun removeBookmark(@Path("id") id: String): ApiResponse<Unit?>

    @GET("experiences/{id}/comments")
    suspend fun getComments(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<CommentDto>>

    @POST("experiences/{id}/comments")
    suspend fun addComment(
        @Path("id") id: String,
        @Body request: CreateCommentRequestDto
    ): ApiResponse<CommentDto>
}

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): ApiResponse<UserDto>

    @PUT("users/me")
    suspend fun updateMe(@Body request: UpdateUserRequestDto): ApiResponse<UserDto>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): ApiResponse<UserDto>

    @GET("users/{id}/experiences")
    suspend fun getUserExperiences(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<ExperienceSummaryDto>>

    @GET("users/me/bookmarks")
    suspend fun getBookmarks(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<ExperienceSummaryDto>>
}

interface MediaApi {
    @POST("media/cloudinary-signature")
    suspend fun getCloudinarySignature(): ApiResponse<CloudinarySignatureDto>
}
