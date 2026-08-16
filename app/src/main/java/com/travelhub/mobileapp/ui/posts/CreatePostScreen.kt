package com.travelhub.mobileapp.ui.posts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.CategoryChip
import com.travelhub.mobileapp.data.api.PostApi
import com.travelhub.mobileapp.data.api.RetrofitClient
import com.travelhub.mobileapp.data.api.SpotApi
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.MockPostRepository
import com.travelhub.mobileapp.data.repository.MockSpotRepository
import com.travelhub.mobileapp.data.repository.RealPostRepository
import com.travelhub.mobileapp.data.repository.RealSpotRepository

@Composable
fun CreatePostScreen(
    editingPostId: Int? = null,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CreatePostViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val preferences = AppPreferences(context.applicationContext)
                val retrofit = RetrofitClient.getInstance(preferences)
                val spotApi = retrofit.create(SpotApi::class.java)
                val postApi = retrofit.create(PostApi::class.java)

                return CreatePostViewModel(
                    postRepository = RealPostRepository(postApi),
                    spotRepository = RealSpotRepository(spotApi),
                    editingPostId = editingPostId
                ) as T
            }
        }
    )

    val caption by viewModel.caption.collectAsState()
    val selectedSpot by viewModel.selectedSpot.collectAsState()
    val availableSpots by viewModel.availableSpots.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) onSubmitSuccess()
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
                if (viewModel.isEditMode) "Edit Post" else "Share Your Travel Story 📸",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp).weight(1f)) {
            // Image upload placeholder — flagged backend gap, see note below
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(onClick = { /* TODO: image picker, pending multipart API */ }) {
                    Text("Add Photo (coming soon)")
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = viewModel::onCaptionChange,
                label = { Text("Caption") },
                placeholder = { Text("Share your journey...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tag a Spot (optional)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryChip(
                        label = "None",
                        selected = selectedSpot == null,
                        onClick = { viewModel.onSpotSelect(null) }
                    )
                }
                items(availableSpots, key = { it.id }) { spot ->
                    CategoryChip(
                        label = spot.name,
                        selected = selectedSpot?.id == spot.id,
                        onClick = { viewModel.onSpotSelect(spot) }
                    )
                }
            }

            if (submitState is SubmitState.Error) {
                Text(
                    (submitState as SubmitState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = submitState !is SubmitState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(48.dp)
        ) {
            if (submitState is SubmitState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (viewModel.isEditMode) "Save Changes" else "Post Experience")
            }
        }
    }
}
