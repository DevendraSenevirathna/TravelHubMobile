package com.travelhub.mobileapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.ui.AppViewModelFactory

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookingHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    when (val state = uiState) {
        is ProfileUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
        is ProfileUiState.Error -> ErrorState(
            message = state.message,
            onRetry = { viewModel.load() },
            modifier = Modifier.fillMaxSize()
        )
        is ProfileUiState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile", style = MaterialTheme.typography.headlineMedium)
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.profile.profileImageUrl != null) {
                                AsyncImage(
                                    model = state.profile.profileImageUrl,
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            state.profile.username,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        if (state.profile.bio.isNotBlank()) {
                            Text(
                                state.profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        OutlinedButton(
                            onClick = onEditProfileClick,
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Edit Profile")
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        ProfileMenuRow("Booking History", onClick = onBookingHistoryClick)
                        Divider()
                        ProfileMenuRow("Favorites", onClick = onFavoritesClick)
                        Divider()
                    }
                }

                item {
                    Text(
                        "My Posts (${state.myPosts.size})",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                if (state.myPosts.isEmpty()) {
                    item {
                        Text(
                            "You haven't posted anything yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
                        )
                    }
                } else {
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .heightIn(max = 400.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.myPosts, key = { it.id }) { post ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = post.imageUrl,
                                        contentDescription = post.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                item {
                    TextButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    title = { Text("Log out?") },
                    text = { Text("You'll need to sign in again to continue.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutConfirm = false
                            viewModel.logout(onLoggedOut)
                        }) { Text("Logout", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Icon(Icons.Filled.ChevronRight, contentDescription = null)
    }
}