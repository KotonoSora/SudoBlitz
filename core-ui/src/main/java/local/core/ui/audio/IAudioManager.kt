package local.core.ui.audio

import androidx.annotation.RawRes

/**
 * Interface for managing background music and sound effects.
 */
interface IAudioManager {
    fun playBgm()
    fun pauseBgm()
    fun stopBgm()
    fun playSfx(@RawRes sfxResId: Int)
    fun release()
    fun updateSettings(musicEnabled: Boolean, sfxEnabled: Boolean)
}
