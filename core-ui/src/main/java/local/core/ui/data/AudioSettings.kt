package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/** Audio-preference port. */
interface AudioSettings {
    val musicEnabledFlow: Flow<Boolean>
    val sfxEnabledFlow: Flow<Boolean>
    suspend fun toggleMusic(enabled: Boolean)
    suspend fun toggleSfx(enabled: Boolean)
}
