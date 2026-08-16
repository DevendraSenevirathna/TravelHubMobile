package com.travelhub.mobileapp.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.PostRepository
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubmitState {
    object Idle : SubmitState()
    object Loading : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

class CreatePostViewModel(
    private val postRepository: PostRepository,
    private val spotRepository: SpotRepository,
    private val editingPostId: Int? // null = create mode, non-null = edit mode
) : ViewModel() {

    private val _caption = MutableStateFlow("")
    val caption: StateFlow<String> = _caption

    private val _selectedSpot = MutableStateFlow<Spot?>(null)
    val selectedSpot: StateFlow<Spot?> = _selectedSpot

    private val _availableSpots = MutableStateFlow<List<Spot>>(emptyList())
    val availableSpots: StateFlow<List<Spot>> = _availableSpots

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    val isEditMode: Boolean get() = editingPostId != null

    init {
        loadSpots()
        if (editingPostId != null) loadExistingPost(editingPostId)
    }

    private fun loadSpots() {
        viewModelScope.launch {
            spotRepository.getAllSpots().onSuccess { _availableSpots.value = it }
        }
    }

    private fun loadExistingPost(postId: Int) {
        viewModelScope.launch {
            postRepository.getPostById(postId).onSuccess { post ->
                _caption.value = post.caption
                _selectedSpot.value = _availableSpots.value.find { it.id == post.spotId }
            }
        }
    }

    fun onCaptionChange(value: String) {
        _caption.value = value
    }

    fun onSpotSelect(spot: Spot?) {
        _selectedSpot.value = spot
    }

    fun submit() {
        if (_caption.value.isBlank()) {
            _submitState.value = SubmitState.Error("Caption can't be empty")
            return
        }

        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            val result = if (editingPostId != null) {
                postRepository.updatePost(editingPostId, _caption.value)
            } else {
                postRepository.createPost(_caption.value, _selectedSpot.value?.id)
            }
            result.fold(
                onSuccess = { _submitState.value = SubmitState.Success },
                onFailure = { e -> _submitState.value = SubmitState.Error(e.message ?: "Something went wrong") }
            )
        }
    }
}
