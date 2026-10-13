package local.sudoku.ui.infrastructure.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.staticCompositionLocalOf
import local.sudoku.ui.R

/**
 * SFX-only sound manager (no BGM). Sanctioned bypass of core `AudioManager`:
 * - Lives in game-ui and owns no preferences of its own.
 * - `soundEnabled` is driven by the host from core `AudioSettings.sfxEnabledFlow`;
 *   toggles route through core `AudioSettings` (see SettingsViewModel).
 * - The core `musicEnabledFlow` toggle is still honored (surfaced in Settings)
 *   even though there is no background music to pause.
 */
class SoundManager(context: Context? = null) {
    private val soundPool: SoundPool?
    private val sounds: Map<String, Int>

    /** Driven from core AudioSettings.sfxEnabledFlow by the host Activity. */
    var soundEnabled: Boolean = true

    init {
        if (context != null) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build()

            sounds = mapOf(
                "tap" to soundPool.load(context, R.raw.tap, 1),
                "error" to soundPool.load(context, R.raw.error, 1),
                "win" to soundPool.load(context, R.raw.win, 1),
                "lose" to soundPool.load(context, R.raw.lose, 1)
            )
        } else {
            soundPool = null
            sounds = emptyMap()
        }
    }

    fun playTap() = play("tap")
    fun playError() = play("error")
    fun playWin() = play("win")
    fun playLose() = play("lose")

    private fun play(name: String) {
        if (!soundEnabled) return
        sounds[name]?.let { id ->
            soundPool?.play(id, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
    }
}

val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    // Provide a no-op SoundManager by default to avoid crashes in Previews
    SoundManager(null)
}
