package com.travelhub.mobileapp.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.repository.MockPostRepository
import com.travelhub.mobileapp.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.travelhub.mobileapp.data.api.PostApi
import com.travelhub.mobileapp.data.api.RetrofitClient
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.RealPostRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.travelhub.mobileapp.data.api.AuthApi
import com.travelhub.mobileapp.ui.RepositoryProvider

sealed class PostDetailsUiState {
    object Loading : PostDetailsUiState()
    data class Success(val post: Post) : PostDetailsUiState()
    object Deleted : PostDetailsUiState()
    data class Error(val message: String) : PostDetailsUiState()
}

class PostDetailsViewModel(
    private val postId: Int,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailsUiState>(PostDetailsUiState.Loading)
    val uiState: StateFlow<PostDetailsUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PostDetailsUiState.Loading
            postRepository.getPostById(postId).fold(
                onSuccess = { _uiState.value = PostDetailsUiState.Success(it) },
                onFailure = { e -> _uiState.value = PostDetailsUiState.Error(e.message ?: "Post not found") }
            )
        }
    }

    fun toggleLike() {
        val current = _uiState.value
        if (current is PostDetailsUiState.Success) {
            viewModelScope.launch {
                postRepository.toggleLike(postId)
                val updated = current.post.copy(
                    isLiked = !current.post.isLiked,
                    likesCount = if (current.post.isLiked) current.post.likesCount - 1 else current.post.likesCount + 1
                )
                _uiState.value = PostDetailsUiState.Success(updated)
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            postRepository.deletePost(postId).onSuccess {
                _uiState.value = PostDetailsUiState.Deleted
            }
        }
    }
}

@Composable
fun PostDetailsScreen(
    postId: Int,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { AppPreferences(context.applicationContext) }
    val retrofit = remember { RetrofitClient.getInstance(preferences) }
    val authApi = remember { retrofit.create(AuthApi::class.java) }
    val profileRepository = remember { RepositoryProvider.getProfileRepository(authApi) }
    val currentProfile by profileRepository.currentProfile.collectAsState()
    val currentUsername = currentProfile?.username

    val viewModel: PostDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val preferences = AppPreferences(context.applicationContext)
                val retrofit = RetrofitClient.getInstance(preferences)
                val postApi = retrofit.create(PostApi::class.java)
                return PostDetailsViewModel(postId, RealPostRepository(postApi)) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is PostDetailsUiState.Deleted) onDeleted()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            val state = uiState
            if (state is PostDetailsUiState.Success && state.post.author == currentUsername) {
                Row {
                    IconButton(onClick = { onEditClick(postId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        when (val state = uiState) {
            is PostDetailsUiState.Loading -> LoadingState(modifier = Modifier.weight(1f).fillMaxWidth())
            is PostDetailsUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.load() },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            is PostDetailsUiState.Deleted -> Unit // handled by LaunchedEffect
            is PostDetailsUiState.Success -> {
                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(state.post.author, style = MaterialTheme.typography.titleMedium)
                        if (state.post.spotName != null) {
                            Text(
                                " · ${state.post.spotName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    AsyncImage(
                        model = state.post.imageUrl,
                        contentDescription = "Post image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleLike() }) {
                            Icon(
                                imageVector = if (state.post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (state.post.isLiked) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text("${state.post.likesCount} likes", style = MaterialTheme.typography.bodyMedium)
                    }

                    Text(
                        state.post.caption,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete post?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deletePost()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
