package com.trailbook.feature.experience.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.trailbook.core.common.UiState
import com.trailbook.core.design.components.ErrorState
import com.trailbook.core.design.components.LoadingState
import com.trailbook.core.design.components.PrimaryButton
import com.trailbook.core.design.components.SectionHeader

@Composable
fun ExperienceDetailScreen(
    onEdit: (String) -> Unit,
    viewModel: ExperienceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }

    when (val s = state) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::load)
        is UiState.Success -> {
            val exp = s.data
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AsyncImage(
                        model = exp.coverImageUrl,
                        contentDescription = exp.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(exp.title, style = MaterialTheme.typography.displaySmall)
                    val dest = exp.destination
                    if (!dest.isNullOrBlank()) {
                        Text(dest, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                    Row {
                        IconButton(onClick = viewModel::toggleLike) {
                            Icon(
                                if (exp.likedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like"
                            )
                        }
                        Text("${exp.likeCount}", modifier = Modifier.padding(top = 12.dp))
                        IconButton(onClick = viewModel::toggleBookmark) {
                            Icon(
                                if (exp.bookmarkedByCurrentUser) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                        IconButton(onClick = viewModel::shareExperience) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
                item {
                    SectionHeader("Overview")
                    Text(exp.overview ?: "No overview provided.")
                }
                if (exp.timeline.isNotEmpty()) {
                    item { SectionHeader("Timeline") }
                    items(exp.timeline) { day ->
                        Text("Day ${day.dayNumber}: ${day.title}", style = MaterialTheme.typography.titleLarge)
                        val desc = day.description
                        if (!desc.isNullOrBlank()) Text(desc)
                    }
                }
                if (exp.budget.isNotEmpty()) {
                    item { SectionHeader("Budget") }
                    items(exp.budget) { item ->
                        Text("${item.category}: ${item.currency} ${item.amount}")
                    }
                }
                if (exp.accommodation.isNotEmpty()) {
                    item { SectionHeader("Accommodation") }
                    items(exp.accommodation) { item ->
                        Text("${item.name} - ${item.location ?: ""}")
                    }
                }
                if (exp.food.isNotEmpty()) {
                    item { SectionHeader("Food") }
                    items(exp.food) { item ->
                        Text("${item.name} (${item.cuisine ?: "Cuisine N/A"})")
                    }
                }
                if (exp.transportation.isNotEmpty()) {
                    item { SectionHeader("Transportation") }
                    items(exp.transportation) { item ->
                        Text("${item.mode}: ${item.details ?: ""}")
                    }
                }
                if (exp.gallery.isNotEmpty()) {
                    item { SectionHeader("Gallery") }
                    items(exp.gallery) { item ->
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.caption,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                if (exp.tips.isNotEmpty()) {
                    item { SectionHeader("Tips") }
                    items(exp.tips) { tip ->
                        Text("• ${tip.content}")
                    }
                }
                if (exp.packing.isNotEmpty()) {
                    item { SectionHeader("Packing List") }
                    items(exp.packing) { item ->
                        Text("${if (item.checked) "☑" else "☐"} ${item.item}")
                    }
                }
                item {
                    SectionHeader("Comments (${exp.commentCount})")
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Add a comment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    PrimaryButton(
                        text = "Post Comment",
                        onClick = {
                            viewModel.addComment(commentText)
                            commentText = ""
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(comments) { comment ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(comment.userName, style = MaterialTheme.typography.labelLarge)
                        Text(comment.content)
                    }
                }
            }
        }
    }
}
