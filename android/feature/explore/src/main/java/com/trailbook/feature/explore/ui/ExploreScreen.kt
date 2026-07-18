package com.trailbook.feature.explore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.trailbook.core.design.components.ExperienceCard

@Composable
fun ExploreScreen(
    onExperienceClick: (String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val sort by viewModel.sort.collectAsStateWithLifecycle()
    val results = viewModel.results.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.updateQuery(it)
            },
            label = { Text("Search experiences") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("recent" to "Recent", "popular" to "Popular")) { (value, label) ->
                FilterChip(
                    selected = sort == value,
                    onClick = { viewModel.updateSort(value) },
                    label = { Text(label) }
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text("Explore", style = MaterialTheme.typography.headlineMedium)
            }
            items(
                count = results.itemCount,
                key = results.itemKey { it.id }
            ) { index ->
                val item = results[index] ?: return@items
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
