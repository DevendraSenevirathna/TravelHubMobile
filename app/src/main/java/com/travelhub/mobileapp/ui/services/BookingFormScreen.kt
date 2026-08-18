package com.travelhub.mobileapp.ui.services

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.data.model.Booking
import com.travelhub.mobileapp.data.repository.BookingRepository
import com.travelhub.mobileapp.data.repository.MockBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BookingSubmitState {
    object Idle : BookingSubmitState()
    object Loading : BookingSubmitState()
    data class Success(val booking: Booking) : BookingSubmitState()
    data class Error(val message: String) : BookingSubmitState()
}

class BookingFormViewModel(
    private val serviceId: Int,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _submitState = MutableStateFlow<BookingSubmitState>(BookingSubmitState.Idle)
    val submitState: StateFlow<BookingSubmitState> = _submitState

    fun submitBooking(date: String, guests: Int, notes: String) {
        if (date.isBlank()) {
            _submitState.value = BookingSubmitState.Error("Please select a date")
            return
        }
        viewModelScope.launch {
            _submitState.value = BookingSubmitState.Loading
            bookingRepository.createBooking(serviceId, date, guests, notes).fold(
                onSuccess = { _submitState.value = BookingSubmitState.Success(it) },
                onFailure = { e -> _submitState.value = BookingSubmitState.Error(e.message ?: "Booking failed") }
            )
        }
    }
}

@Composable
fun BookingFormScreen(
    serviceId: Int,
    onBackClick: () -> Unit,
    onBookingConfirmed: (Int) -> Unit
) {
    val viewModel: BookingFormViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookingFormViewModel(serviceId, MockBookingRepository) as T
            }
        }
    )
    val submitState by viewModel.submitState.collectAsState()

    var date by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(submitState) {
        val state = submitState
        if (state is BookingSubmitState.Success) {
            onBookingConfirmed(state.booking.id)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Book Service", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 8.dp))
        }

        Column(modifier = Modifier.padding(20.dp).weight(1f)) {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (e.g. 2026-09-01)") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = guests,
                onValueChange = { if (it.all { c -> c.isDigit() }) guests = it },
                label = { Text("Guests") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            if (submitState is BookingSubmitState.Error) {
                Text(
                    (submitState as BookingSubmitState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.submitBooking(date, guests.toIntOrNull() ?: 1, notes) },
            enabled = submitState !is BookingSubmitState.Loading,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(48.dp)
        ) {
            if (submitState is BookingSubmitState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Confirm Booking")
            }
        }
    }
}
