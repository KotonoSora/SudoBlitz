package com.jn.numgrid.application.settings

import com.jn.numgrid.domain.settings.Settings
import com.jn.numgrid.domain.settings.SettingsPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveSettingsUseCase(private val gateway: SettingsPreferencesGateway) {
    operator fun invoke(): Flow<Settings> = combine(
        gateway.soundEnabledFlow,
        gateway.musicEnabledFlow
    ) { sound, music ->
        Settings(soundEnabled = sound, musicEnabled = music)
    }
}

class ObserveSoundEnabledUseCase(private val gateway: SettingsPreferencesGateway) {
    operator fun invoke(): Flow<Boolean> = gateway.soundEnabledFlow
}

class ObserveMusicEnabledUseCase(private val gateway: SettingsPreferencesGateway) {
    operator fun invoke(): Flow<Boolean> = gateway.musicEnabledFlow
}

class ToggleSoundUseCase(private val gateway: SettingsPreferencesGateway) {
    suspend operator fun invoke(enabled: Boolean) {
        gateway.updateSoundEnabled(enabled)
    }
}

class ToggleMusicUseCase(private val gateway: SettingsPreferencesGateway) {
    suspend operator fun invoke(enabled: Boolean) {
        gateway.updateMusicEnabled(enabled)
    }
}
