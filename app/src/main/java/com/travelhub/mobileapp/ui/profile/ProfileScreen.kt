package com.travelhub.mobileapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when (val state = uiState) {
        is ProfileUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
        is ProfileUiState.Error -> ErrorState(
            message = state.message,
            onRetry = { viewModel.load() },
            modifier = Modifier.fillMaxSize()
        )
        is ProfileUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9FAF9))
            ) {
                // 1. Top Header (Clean Centered Title)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 2. Profile Avatar & User Details
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color(0xFFE8F5E9), CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!state.profile.profileImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = state.profile.profileImageUrl,
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(52.dp),
                                    tint = Color(0xFF757575)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = state.profile.username,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        if (state.profile.bio.isNotBlank()) {
                            Text(
                                text = "🌿 ${state.profile.bio}",
                                fontSize = 13.5.sp,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = onEditProfileClick,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Edit Profile",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }

                // 3. Menu Navigation Cards
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileMenuCard(
                            icon = Icons.Default.FavoriteBorder,
                            title = "Favorites",
                            subtitle = "Saved trails, spots, and stays",
                            onClick = onFavoritesClick
                        )

                        ProfileMenuCard(
                            icon = Icons.Default.DateRange,
                            title = "Booking History",
                            subtitle = "Past and upcoming trips",
                            onClick = onBookingHistoryClick
                        )

                        ProfileMenuCard(
                            icon = Icons.Default.Settings,
                            title = "Settings",
                            subtitle = "Account, privacy & preferences",
                            onClick = onSettingsClick
                        )

                        ProfileMenuCard(
                            icon = Icons.Default.Collections,
                            title = "My Posts (${state.myPosts.size})",
                            subtitle = "View your shared adventures",
                            onClick = { /* Scroll to posts */ }
                        )
                    }
                }

                // 4. My Posts Photo Grid
                if (state.myPosts.isEmpty()) {
                    item {
                        Text(
                            text = "You haven't posted anything yet.",
                            fontSize = 13.5.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 6.dp)
                                .heightIn(max = 420.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.myPosts, key = { it.id }) { post ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.aspectRatio(1f),
                                    color = Color(0xFFEEEEEE),
                                    shadowElevation = 1.dp
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
                    }
                }

                // 5. Logout Button
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { showLogoutConfirm = true },
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBDBDBD)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            modifier = Modifier
                                .height(46.dp)
                                .defaultMinSize(minWidth = 150.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color(0xFF424242),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log Out",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
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
private fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F6F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}