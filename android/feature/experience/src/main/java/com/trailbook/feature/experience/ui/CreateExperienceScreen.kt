package com.trailbook.feature.experience.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trailbook.core.common.UiState
import com.trailbook.core.design.components.GalleryUploadGrid
import com.trailbook.core.design.components.ImageUploadSection
import com.trailbook.core.design.components.PrimaryButton
import com.trailbook.core.design.components.WizardStepIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExperienceScreen(
    onPublished: (String) -> Unit,
    onCancel: () -> Unit,
    viewModel: CreateExperienceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val publishState by viewModel.publishState.collectAsStateWithLifecycle()

    var pendingUploadTarget by remember { mutableStateOf<Boolean?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val forCover = pendingUploadTarget
        pendingUploadTarget = null
        if (uri != null && forCover != null) {
            viewModel.uploadImageFromUri(uri, forCover)
        }
    }

    var dayNum by remember { mutableIntStateOf(1) }
    var dayTitle by remember { mutableStateOf("") }
    var dayDesc by remember { mutableStateOf("") }
    var budgetCat by remember { mutableStateOf("") }
    var budgetAmt by remember { mutableStateOf("") }
    var accName by remember { mutableStateOf("") }
    var accLoc by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var foodCuisine by remember { mutableStateOf("") }
    var tipText by remember { mutableStateOf("") }
    var packItem by remember { mutableStateOf("") }

    fun pickImage(forCover: Boolean) {
        pendingUploadTarget = forCover
        imagePickerLauncher.launch("image/*")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (state.experienceId == null) "Create Experience" else "Edit Experience") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            WizardStepIndicator(state.currentStep, state.totalSteps)
            if (state.isSaving) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.height(16.dp))
                    Text("Saving draft...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            when (state.currentStep) {
                0 -> {
                    Text("Basic Information", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.overview,
                        onValueChange = viewModel::updateOverview,
                        label = { Text("Overview") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ImageUploadSection(
                        title = "Cover Image",
                        imageUrl = state.coverImageUrl.takeIf { it.isNotBlank() },
                        isUploading = state.isUploading,
                        onPickImage = { pickImage(forCover = true) }
                    )
                }
                1 -> {
                    Text("Destination", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = state.destination,
                        onValueChange = viewModel::updateDestination,
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                2 -> {
                    Text("Timeline", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = dayNum.toString(),
                        onValueChange = { dayNum = it.toIntOrNull() ?: 1 },
                        label = { Text("Day") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dayTitle,
                        onValueChange = { dayTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dayDesc,
                        onValueChange = { dayDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addTimelineEntry(dayNum, dayTitle, dayDesc)
                        dayTitle = ""
                        dayDesc = ""
                    }) {
                        Text("Add Day")
                    }
                    state.timeline.forEach { Text("Day ${it.dayNumber}: ${it.title}") }
                }
                3 -> {
                    Text("Budget", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = budgetCat,
                        onValueChange = { budgetCat = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = budgetAmt,
                        onValueChange = { budgetAmt = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addBudget(budgetCat, budgetAmt.toDoubleOrNull() ?: 0.0)
                        budgetCat = ""
                        budgetAmt = ""
                    }) { Text("Add Budget Item") }
                    state.budget.forEach { Text("${it.category}: ${it.amount}") }
                }
                4 -> {
                    Text("Accommodation", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = accName,
                        onValueChange = { accName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accLoc,
                        onValueChange = { accLoc = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addAccommodation(accName, accLoc)
                        accName = ""
                        accLoc = ""
                    }) { Text("Add") }
                    state.accommodation.forEach { Text(it.name) }
                }
                5 -> {
                    Text("Food", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = foodCuisine,
                        onValueChange = { foodCuisine = it },
                        label = { Text("Cuisine") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addFood(foodName, foodCuisine)
                        foodName = ""
                        foodCuisine = ""
                    }) { Text("Add") }
                    state.food.forEach { Text(it.name) }
                }
                6 -> {
                    GalleryUploadGrid(
                        imageUrls = state.gallery.map { it.imageUrl },
                        isUploading = state.isUploading,
                        onPickImage = { pickImage(forCover = false) }
                    )
                }
                7 -> {
                    Text("Tips & Packing", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = tipText,
                        onValueChange = { tipText = it },
                        label = { Text("Tip") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addTip(tipText)
                        tipText = ""
                    }) { Text("Add Tip") }
                    OutlinedTextField(
                        value = packItem,
                        onValueChange = { packItem = it },
                        label = { Text("Packing item") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {
                        viewModel.addPacking(packItem)
                        packItem = ""
                    }) { Text("Add Packing Item") }
                }
                8 -> {
                    Text("Review & Publish", style = MaterialTheme.typography.titleLarge)
                    Text("Title: ${state.title}")
                    Text("Destination: ${state.destination}")
                    Text("Timeline entries: ${state.timeline.size}")
                    Text("Gallery images: ${state.gallery.size}")
                    if (state.coverImageUrl.isNotBlank()) {
                        Text("Cover image: uploaded")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (state.currentStep > 0) {
                    TextButton(onClick = viewModel::previousStep) { Text("Back") }
                }
                if (state.currentStep < state.totalSteps - 1) {
                    PrimaryButton("Next", viewModel::nextStep, modifier = Modifier.weight(1f))
                } else {
                    PrimaryButton(
                        text = if (publishState is UiState.Loading) "Publishing..." else "Publish",
                        onClick = { viewModel.publish(onPublished) },
                        enabled = publishState !is UiState.Loading,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            TextButton(onClick = { viewModel.saveDraft { } }) {
                Text(if (state.isSaving) "Saving..." else "Save Draft to server")
            }
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}
