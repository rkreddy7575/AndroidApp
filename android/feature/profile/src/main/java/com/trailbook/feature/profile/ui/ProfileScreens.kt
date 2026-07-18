package com.trailbook.feature.profile.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.trailbook.core.common.UiState
import com.trailbook.core.design.components.ErrorState
import com.trailbook.core.design.components.ExperienceCard
import com.trailbook.core.design.components.LoadingState
import com.trailbook.core.design.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onExperienceClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val myExperiences = viewModel.myExperiences.collectAsLazyPagingItems()

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
        when (val state = userState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = state.message, onRetry = viewModel::loadProfile)
            is UiState.Success -> {
                val user = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(user.displayName, style = MaterialTheme.typography.headlineMedium)
                            Text("@${user.username}", style = MaterialTheme.typography.bodyMedium)
                            user.bio?.let { Text(it) }
                            PrimaryButton("Settings", onNavigateToSettings)
                            PrimaryButton("Logout", { viewModel.logout(onLogout) })
                        }
                    }
                    item {
                        Text(
                            "My Experiences",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (myExperiences.itemCount == 0) {
                        item {
                            Text(
                                "No published experiences yet. Create your first journey!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    } else {
                        items(
                            count = myExperiences.itemCount,
                            key = myExperiences.itemKey { it.id }
                        ) { index ->
                            val item = myExperiences[index] ?: return@items
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
    }
}

@Composable
fun BookmarksScreen(
    onExperienceClick: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Bookmarks", style = MaterialTheme.typography.headlineMedium) }
        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { it.id }
        ) { index ->
            val item = bookmarks[index] ?: return@items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isDark = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Theme", style = MaterialTheme.typography.titleLarge)
            listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                FilterChip(
                    selected = themeMode == value,
                    onClick = { viewModel.setTheme(value) },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                if (isDark) "Dark mode active" else "Light mode active",
                style = MaterialTheme.typography.bodyMedium
            )
            PrimaryButton("Back", onBack, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
