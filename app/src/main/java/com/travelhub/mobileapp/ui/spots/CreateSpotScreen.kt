package com.travelhub.mobileapp.ui.spots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.CategoryChip
import com.travelhub.mobileapp.data.api.RetrofitClient
import com.travelhub.mobileapp.data.api.SpotApi
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.RealSpotRepository
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.travelhub.mobileapp.data.api.uriToImagePart

@Composable
fun CreateSpotScreen(
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CreateSpotViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val preferences = AppPreferences(context.applicationContext)
                val retrofit = RetrofitClient.getInstance(preferences)
                val spotApi = retrofit.create(SpotApi::class.java)
                return CreateSpotViewModel(RealSpotRepository(spotApi)) as T
            }
        }
    )

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onImageSelected(uri) }

    LaunchedEffect(submitState) {
        if (submitState is CreateSpotState.Success) onSubmitSuccess()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Create a New Travel Spot 🌍",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Your spot will be reviewed before it becomes visible to the community.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Image picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(bottom = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val selectedImageUri by viewModel.selectedImageUri.collectAsState()
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                        Text("Tap to add a photo")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Spot Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Category", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(spotCategoryOptions) { option ->
                    CategoryChip(
                        label = option.replaceFirstChar { it.uppercase() },
                        selected = option == category,
                        onClick = { viewModel.onCategorySelect(option) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Location", style = MaterialTheme.typography.titleMedium)
            Text(
                "Enter coordinates manually for now — map picker coming in a future update.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = latitude,
                onValueChange = viewModel::onLatitudeChange,
                label = { Text("Latitude") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = longitude,
                onValueChange = viewModel::onLongitudeChange,
                label = { Text("Longitude") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            if (submitState is CreateSpotState.Error) {
                Text(
                    (submitState as CreateSpotState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                viewModel.submit { uri -> uriToImagePart(context, uri) }
            },
            enabled = submitState !is CreateSpotState.Loading,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(48.dp)
        ) {
            if (submitState is CreateSpotState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Submit for Review")
            }
        }
    }
}