package nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.repository.AvatarRepository

class ProfileViewModel(
    private val avatarRepository: AvatarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadAvatar()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnModeSelected -> _state.update { it.copy(selectedMode = action.mode) }
            ProfileAction.OnFollowClick -> Unit
        }
    }

    private fun loadAvatar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val bytes = avatarRepository.loadAvatar(_state.value.profile.avatarAsset)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            _state.update { it.copy(avatar = bitmap, isLoading = false) }
        }
    }
}