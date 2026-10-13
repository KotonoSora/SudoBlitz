package local.core.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes

class AudioManager(
    context: Context,
    @RawRes private val bgmResId: Int,
    sfxResIds: Collection<Int>
) : IAudioManager {

    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var loadedSfx = mutableMapOf<Int, Int>()

    private var musicEnabled: Boolean = true
    private var sfxEnabled: Boolean = true

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        try {
            bgmPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                val afd = context.resources.openRawResourceFd(bgmResId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = true
                setVolume(0.5f, 0.5f)
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            bgmPlayer = MediaPlayer.create(context, bgmResId)?.apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        sfxResIds.forEach { resId ->
            try {
                loadedSfx[resId] = soundPool?.load(context, resId, 1) ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun playBgm() {
        if (musicEnabled && bgmPlayer?.isPlaying == false) {
            bgmPlayer?.start()
        }
    }

    override fun pauseBgm() {
        bgmPlayer?.pause()
    }

    override fun stopBgm() {
        bgmPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
            player.seekTo(0)
        }
    }

    override fun playSfx(@RawRes sfxResId: Int) {
        if (!sfxEnabled) return
        val sampleId = loadedSfx[sfxResId] ?: return
        soundPool?.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    override fun release() {
        bgmPlayer?.release()
        bgmPlayer = null
        soundPool?.release()
        soundPool = null
        loadedSfx.clear()
    }

    override fun updateSettings(musicEnabled: Boolean, sfxEnabled: Boolean) {
        val wasPlaying = bgmPlayer?.isPlaying == true

        this.musicEnabled = musicEnabled
        this.sfxEnabled = sfxEnabled

        if (!musicEnabled) {
            if (wasPlaying) {
                pauseBgm()
            }
        }
    }
}