package com.jn.numgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jn.numgrid.application.settings.ObserveSettingsUseCase
import com.jn.numgrid.application.settings.ToggleMusicUseCase
import com.jn.numgrid.application.settings.ToggleSoundUseCase
import com.jn.numgrid.data.SettingsPreferencesGatewayAdapter
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.settings.Settings
import com.jn.numgrid.domain.settings.SettingsPreferencesGateway
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeSettings: ObserveSettingsUseCase,
    private val toggleSoundUseCase: ToggleSoundUseCase,
    private val toggleMusicUseCase: ToggleMusicUseCase
) : ViewModel() {

    val settings: StateFlow<Settings> = observeSettings().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Settings(soundEnabled = true, musicEnabled = true)
    )

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            toggleSoundUseCase(enabled)
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            toggleMusicUseCase(enabled)
        }
    }

    companion object {
        fun provideFactory(repository: UserPreferencesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val gateway: SettingsPreferencesGateway =
                        SettingsPreferencesGatewayAdapter(repository)
                    return SettingsViewModel(
                        observeSettings = ObserveSettingsUseCase(gateway),
                        toggleSoundUseCase = ToggleSoundUseCase(gateway),
                        toggleMusicUseCase = ToggleMusicUseCase(gateway)
                    ) as T
                }
            }
    }
}
