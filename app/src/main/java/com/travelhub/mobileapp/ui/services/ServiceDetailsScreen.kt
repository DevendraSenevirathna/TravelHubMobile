package com.travelhub.mobileapp.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.travelhub.mobileapp.data.model.Service
import com.travelhub.mobileapp.data.repository.BookingRepository
import com.travelhub.mobileapp.data.repository.MockBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ServiceDetailsUiState {
    object Loading : ServiceDetailsUiState()
    data class Success(val service: Service) : ServiceDetailsUiState()
    data class Error(val message: String) : ServiceDetailsUiState()
}

class ServiceDetailsViewModel(
    private val serviceId: Int,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ServiceDetailsUiState>(ServiceDetailsUiState.Loading)
    val uiState: StateFlow<ServiceDetailsUiState> = _uiState

    init {
        viewModelScope.launch {
            bookingRepository.getServiceById(serviceId).fold(
                onSuccess = { _uiState.value = ServiceDetailsUiState.Success(it) },
                onFailure = { e -> _uiState.value = ServiceDetailsUiState.Error(e.message ?: "Service not found") }
            )
        }
    }
}

@Composable
fun ServiceDetailsScreen(
    serviceId: Int,
    onBackClick: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val viewModel: ServiceDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceDetailsViewModel(serviceId, MockBookingRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        when (val state = uiState) {
            is ServiceDetailsUiState.Loading -> LoadingState(modifier = Modifier.weight(1f).fillMaxWidth())
            is ServiceDetailsUiState.Error -> ErrorState(message = state.message, modifier = Modifier.weight(1f).fillMaxWidth())
            is ServiceDetailsUiState.Success -> {
                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AsyncImage(
                        model = state.service.imageUrl,
                        contentDescription = state.service.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(state.service.name, style = MaterialTheme.typography.headlineMedium)
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            Text(
                                String.format("%.1f", state.service.rating),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 4.dp, end = 12.dp)
                            )
                            Text(state.service.location, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            state.service.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            "$${state.service.pricePerUnit.toInt()} ${state.service.priceUnit}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
                Button(
                    onClick = { onBookClick(state.service.id) },
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(48.dp)
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}
