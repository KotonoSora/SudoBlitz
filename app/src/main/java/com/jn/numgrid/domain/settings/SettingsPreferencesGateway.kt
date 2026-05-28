package com.jn.numgrid.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsPreferencesGateway {
    val soundEnabledFlow: Flow<Boolean>
    val musicEnabledFlow: Flow<Boolean>

    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun updateMusicEnabled(enabled: Boolean)
}

