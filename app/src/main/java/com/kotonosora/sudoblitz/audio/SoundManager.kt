package com.kotonosora.sudoblitz.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.kotonosora.sudoblitz.R
import com.kotonosora.sudoblitz.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SoundManager(context: Context, private val repository: UserPreferencesRepository) {
    private val soundPool: SoundPool
    private var tapSoundId: Int = 0
    private var errorSoundId: Int = 0
    private var winSoundId: Int = 0
    private var loseSoundId: Int = 0
    
    private var soundEnabled = true
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        tapSoundId = soundPool.load(context, R.raw.tap, 1)
        errorSoundId = soundPool.load(context, R.raw.error, 1)
        winSoundId = soundPool.load(context, R.raw.win, 1)
        loseSoundId = soundPool.load(context, R.raw.lose, 1)
        
        scope.launch {
            repository.soundEnabledFlow.collect {
                soundEnabled = it
            }
        }
    }

    fun playTap() {
        if (soundEnabled) {
            soundPool.play(tapSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playError() {
        if (soundEnabled) {
            soundPool.play(errorSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playWin() {
        if (soundEnabled) {
            soundPool.play(winSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playLose() {
        if (soundEnabled) {
            soundPool.play(loseSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
