package com.travelhub.mobileapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.ui.AppViewModelFactory
import com.travelhub.mobileapp.ui.onboarding.interestOptions
import androidx.compose.ui.platform.LocalContext
import com.travelhub.mobileapp.components.CategoryChip

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: EditProfileViewModel = viewModel(factory = AppViewModelFactory(context))
    val bio by viewModel.bio.collectAsState()
    val interests by viewModel.interests.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(saveState) {
        if (saveState is EditProfileState.Success) onSaved()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Edit Profile", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 8.dp))
        }

        Column(modifier = Modifier.padding(20.dp).weight(1f)) {
            OutlinedTextField(
                value = bio,
                onValueChange = viewModel::onBioChange,
                label = { Text("Bio") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Interests", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(interestOptions) { option ->
                    CategoryChip(
                        label = "${option.emoji} ${option.label}",
                        selected = option.label in interests,
                        onClick = { viewModel.toggleInterest(option.label) }
                    )
                }
            }

            if (saveState is EditProfileState.Error) {
                Text(
                    (saveState as EditProfileState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.save() },
            enabled = saveState !is EditProfileState.Loading,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(48.dp)
        ) {
            if (saveState is EditProfileState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Changes")
            }
        }
    }
}
