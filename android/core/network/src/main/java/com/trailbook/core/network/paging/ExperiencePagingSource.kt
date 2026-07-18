package com.trailbook.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.trailbook.core.network.model.ApiResponse
import com.trailbook.core.network.model.ExperienceSummaryDto
import com.trailbook.core.network.model.PageResponse

class ExperiencePagingSource(
    private val fetch: suspend (page: Int, size: Int) -> ApiResponse<PageResponse<ExperienceSummaryDto>>
) : PagingSource<Int, ExperienceSummaryDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExperienceSummaryDto> {
        val page = params.key ?: 0
        return try {
            val response = fetch(page, params.loadSize)
            val data = response.data ?: return LoadResult.Error(IllegalStateException(response.message))
            LoadResult.Page(
                data = data.content,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.last) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ExperienceSummaryDto>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}
