package com.trailbook.feature.profile.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.trailbook.core.common.Result
import com.trailbook.core.network.api.UserApi
import com.trailbook.core.network.model.ExperienceSummaryDto
import com.trailbook.core.network.model.UserDto
import com.trailbook.core.network.safeApiCall
import com.trailbook.core.network.paging.ExperiencePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getMe(): Result<UserDto> = safeApiCall { userApi.getMe() }

    fun getBookmarks(): Flow<PagingData<ExperienceSummaryDto>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            ExperiencePagingSource { page, size -> userApi.getBookmarks(page, size) }
        }
    ).flow

    fun getMyExperiences(userId: String): Flow<PagingData<ExperienceSummaryDto>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            ExperiencePagingSource { page, size -> userApi.getUserExperiences(userId, page, size) }
        }
    ).flow
}
