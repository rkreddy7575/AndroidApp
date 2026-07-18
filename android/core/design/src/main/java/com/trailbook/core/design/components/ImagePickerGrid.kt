package com.trailbook.core.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageUploadSection(
    title: String,
    imageUrl: String?,
    isUploading: Boolean,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        OutlinedButton(
            onClick = onPickImage,
            enabled = !isUploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isUploading) "Uploading..." else "Pick image from gallery")
        }
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun GalleryUploadGrid(
    imageUrls: List<String>,
    isUploading: Boolean,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Gallery", style = MaterialTheme.typography.titleLarge)
        OutlinedButton(
            onClick = onPickImage,
            enabled = !isUploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isUploading) "Uploading..." else "Add photo")
        }
        if (imageUrls.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(((imageUrls.size / 2 + 1) * 120).coerceAtLeast(120).dp)
            ) {
                items(imageUrls) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Gallery image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
