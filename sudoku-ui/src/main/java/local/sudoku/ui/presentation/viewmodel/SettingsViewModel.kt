package local.sudoku.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.ToggleAudioUseCase

data class AudioState(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true
)

class SettingsViewModel(
    getAudioSettings: GetAudioSettingsUseCase,
    private val toggleAudioUseCase: ToggleAudioUseCase
) : ViewModel() {

    val settings: StateFlow<AudioState> = combine(
        getAudioSettings.sfxEnabledFlow,
        getAudioSettings.musicEnabledFlow
    ) { sound, music ->
        AudioState(soundEnabled = sound, musicEnabled = music)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AudioState())

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            toggleAudioUseCase.toggleSfx(enabled)
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            toggleAudioUseCase.toggleMusic(enabled)
        }
    }
}
