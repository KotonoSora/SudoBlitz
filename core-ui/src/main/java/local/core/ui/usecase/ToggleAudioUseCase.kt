package local.core.ui.usecase

import local.core.ui.data.AudioSettings

class ToggleAudioUseCase(private val audio: AudioSettings) {
    suspend fun toggleMusic(enabled: Boolean) = audio.toggleMusic(enabled)
    suspend fun toggleSfx(enabled: Boolean) = audio.toggleSfx(enabled)
}
