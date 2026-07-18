package com.trailbook.feature.explore.data

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
class ExploreRepository @Inject constructor(
    private val experienceApi: ExperienceApi
) {
    fun search(query: String?, destination: String?, sort: String): Flow<PagingData<ExperienceSummaryDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                ExperiencePagingSource { page, size ->
                    experienceApi.search(query, destination, sort, page, size)
                }
            }
        ).flow
}
