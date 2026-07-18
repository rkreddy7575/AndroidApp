package com.trailbook.core.network.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val last: Boolean
)

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val createdAt: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class ExperienceSummaryDto(
    val id: String,
    val title: String,
    val overview: String?,
    val destination: String?,
    val coverImageUrl: String?,
    val status: String,
    val likeCount: Int,
    val commentCount: Int,
    val author: UserDto,
    val createdAt: String,
    val likedByCurrentUser: Boolean,
    val bookmarkedByCurrentUser: Boolean
)

data class TimelineEntryDto(
    val id: String?,
    val dayNumber: Int,
    val title: String,
    val description: String?,
    val orderIndex: Int
)

data class BudgetItemDto(
    val id: String?,
    val category: String,
    val amount: Double,
    val currency: String,
    val notes: String?
)

data class AccommodationDto(
    val id: String?,
    val name: String,
    val location: String?,
    val cost: Double?,
    val notes: String?
)

data class FoodSpotDto(
    val id: String?,
    val name: String,
    val cuisine: String?,
    val cost: Double?,
    val notes: String?
)

data class TransportationDto(
    val id: String?,
    val mode: String,
    val details: String?,
    val cost: Double?
)

data class GalleryItemDto(
    val id: String?,
    val imageUrl: String,
    val caption: String?,
    val orderIndex: Int
)

data class VideoDto(
    val id: String?,
    val videoUrl: String,
    val thumbnailUrl: String?
)

data class TipDto(
    val id: String?,
    val content: String
)

data class PackingItemDto(
    val id: String?,
    val item: String,
    val checked: Boolean
)

data class ExperienceDetailDto(
    val id: String,
    val title: String,
    val overview: String?,
    val destination: String?,
    val coverImageUrl: String?,
    val status: String,
    val likeCount: Int,
    val commentCount: Int,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val likedByCurrentUser: Boolean,
    val bookmarkedByCurrentUser: Boolean,
    val timeline: List<TimelineEntryDto>,
    val budget: List<BudgetItemDto>,
    val accommodation: List<AccommodationDto>,
    val food: List<FoodSpotDto>,
    val transportation: List<TransportationDto>,
    val gallery: List<GalleryItemDto>,
    val videos: List<VideoDto>,
    val tips: List<TipDto>,
    val packing: List<PackingItemDto>
)

data class CreateExperienceRequestDto(
    val title: String,
    val overview: String? = null,
    val destination: String? = null,
    val coverImageUrl: String? = null,
    val timeline: List<TimelineEntryDto>? = null,
    val budget: List<BudgetItemDto>? = null,
    val accommodation: List<AccommodationDto>? = null,
    val food: List<FoodSpotDto>? = null,
    val transportation: List<TransportationDto>? = null,
    val gallery: List<GalleryItemDto>? = null,
    val videos: List<VideoDto>? = null,
    val tips: List<TipDto>? = null,
    val packing: List<PackingItemDto>? = null
)

data class CommentDto(
    val id: String,
    val experienceId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val content: String,
    val createdAt: String
)

data class CreateCommentRequestDto(val content: String)

data class CloudinarySignatureDto(
    val cloudName: String,
    val apiKey: String,
    val timestamp: Long,
    val signature: String
)

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RefreshRequestDto(val refreshToken: String)

data class UpdateUserRequestDto(
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null
)
