package com.jn.numgrid.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.jn.numgrid.R
import com.jn.numgrid.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager(
    context: Context,
    private val repository: UserPreferencesRepository,
    isPreview: Boolean = false,
) {
    private val soundPool: SoundPool?
    private var tapSoundId: Int = 0
    private var errorSoundId: Int = 0
    private var winSoundId: Int = 0
    private var loseSoundId: Int = 0

    private var soundEnabled = true
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        var tempSoundPool: SoundPool? = null
        if (!isPreview) {
            try {
                val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

                tempSoundPool =
                    SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()

                tapSoundId = tempSoundPool.load(context, R.raw.tap, 1)
                errorSoundId = tempSoundPool.load(context, R.raw.error, 1)
                winSoundId = tempSoundPool.load(context, R.raw.win, 1)
                loseSoundId = tempSoundPool.load(context, R.raw.lose, 1)
            } catch (e: Throwable) {
                // Safely catch NoClassDefFoundError / ClassNotFoundException or other exceptions in Compose Previews / Layoutlib
                e.printStackTrace()
            }
        }
        soundPool = tempSoundPool

        scope.launch {
            repository.soundEnabledFlow.collect {
                soundEnabled = it
            }
        }
    }

    fun playTap() {
        if (soundEnabled) {
            soundPool?.play(tapSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playError() {
        if (soundEnabled) {
            soundPool?.play(errorSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playWin() {
        if (soundEnabled) {
            soundPool?.play(winSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playLose() {
        if (soundEnabled) {
            soundPool?.play(loseSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
    }
}
