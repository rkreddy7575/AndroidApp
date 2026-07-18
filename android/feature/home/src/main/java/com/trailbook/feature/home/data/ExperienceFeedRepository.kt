package com.trailbook.feature.home.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.trailbook.core.network.api.ExperienceApi
import com.trailbook.core.network.model.ExperienceSummaryDto
import com.trailbook.core.network.paging.ExperiencePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceFeedRepository @Inject constructor(
    private val experienceApi: ExperienceApi
) {
    fun getFeed(): Flow<PagingData<ExperienceSummaryDto>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            ExperiencePagingSource { page, size -> experienceApi.getFeed(page, size) }
        }
    ).flow
}
