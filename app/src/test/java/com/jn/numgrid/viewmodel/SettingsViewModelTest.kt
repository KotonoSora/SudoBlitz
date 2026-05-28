package com.jn.numgrid.viewmodel

import com.jn.numgrid.application.settings.ObserveSettingsUseCase
import com.jn.numgrid.application.settings.ToggleMusicUseCase
import com.jn.numgrid.application.settings.ToggleSoundUseCase
import com.jn.numgrid.domain.settings.SettingsPreferencesGateway
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `toggle methods update settings through use cases`() = runTest {
        val gateway = FakeSettingsPreferencesGateway(soundEnabled = false, musicEnabled = true)
        val viewModel = SettingsViewModel(
            observeSettings = ObserveSettingsUseCase(gateway),
            toggleSoundUseCase = ToggleSoundUseCase(gateway),
            toggleMusicUseCase = ToggleMusicUseCase(gateway)
        )

        viewModel.toggleSound(true)
        viewModel.toggleMusic(false)
        advanceUntilIdle()

        assertTrue(viewModel.settings.value.soundEnabled)
        assertFalse(viewModel.settings.value.musicEnabled)
    }

    private class FakeSettingsPreferencesGateway(
        soundEnabled: Boolean,
        musicEnabled: Boolean
    ) : SettingsPreferencesGateway {
        private val soundState = MutableStateFlow(soundEnabled)
        private val musicState = MutableStateFlow(musicEnabled)

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
