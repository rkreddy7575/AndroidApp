package com.trailbook.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.trailbook.core.design.components.ErrorState
import com.trailbook.core.design.components.ExperienceCard
import com.trailbook.core.design.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onExperienceClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val experiences = viewModel.feed.collectAsLazyPagingItems()

    PullToRefreshBox(
        isRefreshing = experiences.loadState.refresh is LoadState.Loading && experiences.itemCount > 0,
        onRefresh = { experiences.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            experiences.loadState.refresh is LoadState.Loading && experiences.itemCount == 0 -> LoadingState()
            experiences.loadState.refresh is LoadState.Error -> ErrorState(
                message = (experiences.loadState.refresh as LoadState.Error).error.message ?: "Failed to load feed",
                onRetry = { experiences.retry() }
            )
            experiences.itemCount == 0 -> ErrorState(message = "No experiences yet. Be the first to share!")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Discover Experiences", style = MaterialTheme.typography.headlineMedium)
                }
                items(
                    count = experiences.itemCount,
                    key = experiences.itemKey { it.id }
                ) { index ->
                    val item = experiences[index] ?: return@items
                    ExperienceCard(
                        title = item.title,
                        destination = item.destination,
                        authorName = item.author.displayName,
                        coverImageUrl = item.coverImageUrl,
                        likeCount = item.likeCount,
                        onClick = { onExperienceClick(item.id) }
                    )
                }
            }
        }
    }
}
