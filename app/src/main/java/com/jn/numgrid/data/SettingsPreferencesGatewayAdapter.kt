package com.jn.numgrid.data

import com.jn.numgrid.domain.settings.SettingsPreferencesGateway
import kotlinx.coroutines.flow.Flow

class SettingsPreferencesGatewayAdapter(
    private val repository: UserPreferencesRepository
) : SettingsPreferencesGateway {
    override val soundEnabledFlow: Flow<Boolean>
        get() = repository.soundEnabledFlow

    override val musicEnabledFlow: Flow<Boolean>
        get() = repository.musicEnabledFlow

    override suspend fun updateSoundEnabled(enabled: Boolean) {
        repository.updateSoundEnabled(enabled)
    }

    override suspend fun updateMusicEnabled(enabled: Boolean) {
        repository.updateMusicEnabled(enabled)
    }
}

