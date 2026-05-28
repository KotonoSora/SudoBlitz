package com.jn.numgrid.application.settings

import com.jn.numgrid.domain.settings.SettingsPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsUseCasesTest {

    @Test
    fun `observe settings use case combines gateway values`() = runTest {
        val gateway = FakeSettingsPreferencesGateway(soundEnabled = true, musicEnabled = false)
        val useCase = ObserveSettingsUseCase(gateway)

        val settings = useCase().first()

        assertTrue(settings.soundEnabled)
        assertFalse(settings.musicEnabled)
    }

    @Test
    fun `observe individual use cases expose latest settings values`() = runTest {
        val gateway = FakeSettingsPreferencesGateway(soundEnabled = true, musicEnabled = false)

        val sound = ObserveSoundEnabledUseCase(gateway).invoke().first()
        val music = ObserveMusicEnabledUseCase(gateway).invoke().first()

        assertTrue(sound)
        assertFalse(music)
    }

    @Test
    fun `toggle use cases update gateway state`() = runTest {
        val gateway = FakeSettingsPreferencesGateway(soundEnabled = false, musicEnabled = true)

        ToggleSoundUseCase(gateway).invoke(true)
        ToggleMusicUseCase(gateway).invoke(false)

        assertTrue(gateway.soundState.value)
        assertFalse(gateway.musicState.value)
    }

    private class FakeSettingsPreferencesGateway(
        soundEnabled: Boolean,
        musicEnabled: Boolean
    ) : SettingsPreferencesGateway {
        val soundState = MutableStateFlow(soundEnabled)
        val musicState = MutableStateFlow(musicEnabled)

        override val soundEnabledFlow: Flow<Boolean> = soundState
        override val musicEnabledFlow: Flow<Boolean> = musicState

        override suspend fun updateSoundEnabled(enabled: Boolean) {
            soundState.value = enabled
        }

        override suspend fun updateMusicEnabled(enabled: Boolean) {
            musicState.value = enabled
        }
    }
}
