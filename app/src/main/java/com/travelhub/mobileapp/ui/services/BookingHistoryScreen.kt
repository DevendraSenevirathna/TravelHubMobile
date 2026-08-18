package com.travelhub.mobileapp.ui.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.EmptyState
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.data.model.Booking
import com.travelhub.mobileapp.data.model.BookingStatus
import com.travelhub.mobileapp.data.repository.BookingRepository
import com.travelhub.mobileapp.data.repository.MockBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BookingHistoryUiState {
    object Loading : BookingHistoryUiState()
    data class Success(val bookings: List<Booking>) : BookingHistoryUiState()
    data class Error(val message: String) : BookingHistoryUiState()
}

class BookingHistoryViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<BookingHistoryUiState>(BookingHistoryUiState.Loading)
    val uiState: StateFlow<BookingHistoryUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = BookingHistoryUiState.Loading
            bookingRepository.getBookingHistory().fold(
                onSuccess = { _uiState.value = BookingHistoryUiState.Success(it) },
                onFailure = { e -> _uiState.value = BookingHistoryUiState.Error(e.message ?: "Couldn't load bookings") }
            )
        }
    }

    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
            load()
        }
    }
}

@Composable
fun BookingHistoryScreen() {
    val viewModel: BookingHistoryViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookingHistoryViewModel(MockBookingRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Booking History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(20.dp)
        )

        when (val state = uiState) {
            is BookingHistoryUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is BookingHistoryUiState.Error -> ErrorState(message = state.message, modifier = Modifier.fillMaxSize())
            is BookingHistoryUiState.Success -> {
                if (state.bookings.isEmpty()) {
                    EmptyState(
                        message = "No bookings yet",
                        subtitle = "Your booked services will appear here",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.bookings, key = { it.id }) { booking ->
                            BookingCard(booking = booking, onCancel = { viewModel.cancelBooking(booking.id) })
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(booking.serviceName, style = MaterialTheme.typography.titleMedium)
                StatusBadge(booking.status)
            }
            Text("Date: ${booking.date}", style = MaterialTheme.typography.bodyMedium)
            Text("Guests: ${booking.guests}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Total: $${String.format("%.2f", booking.totalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (booking.status == BookingStatus.CONFIRMED || booking.status == BookingStatus.PENDING) {
                TextButton(onClick = onCancel, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Cancel Booking", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val (label, color) = when (status) {
        BookingStatus.PENDING -> "Pending" to Color(0xFFF9A825)
        BookingStatus.CONFIRMED -> "Confirmed" to Color(0xFF2E7D32)
        BookingStatus.CANCELLED -> "Cancelled" to Color(0xFFB00020)
        BookingStatus.COMPLETED -> "Completed" to Color(0xFF4FC3F7)
    }
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}
