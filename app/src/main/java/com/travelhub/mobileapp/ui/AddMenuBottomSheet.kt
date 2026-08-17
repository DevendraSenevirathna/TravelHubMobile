package com.travelhub.mobileapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuBottomSheet(
    onDismiss: () -> Unit,
    onCreateSpotClick: () -> Unit,
    onUploadPostClick: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                "What would you like to share?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            AddMenuOption(
                icon = Icons.Filled.AddLocation,
                title = "Create New Spot",
                subtitle = "Share a new hidden gem and help others discover amazing places.",
                onClick = onCreateSpotClick
            )

            AddMenuOption(
                icon = Icons.Filled.PhotoCamera,
                title = "Upload Post",
                subtitle = "Share your travel experience with the community instantly.",
                onClick = onUploadPostClick
            )
        }
    }
}

@Composable
private fun AddMenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
