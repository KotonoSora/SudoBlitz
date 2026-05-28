package com.jn.numgrid

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.viewmodel.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GameFlowInstrumentedTest {

    private lateinit var preferencesFile: File
    private lateinit var dataStoreScope: CoroutineScope
    private lateinit var repository: UserPreferencesRepository
    private lateinit var fakeGameRecordDao: InMemoryGameRecordDao

    @Before
    fun setup() {
        val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()
        preferencesFile =
            File(appContext.filesDir, "android-test-${UUID.randomUUID()}.preferences_pb")
        dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { preferencesFile }
        )

        repository = UserPreferencesRepository(dataStore)
        fakeGameRecordDao = InMemoryGameRecordDao()
    }

    @After
    fun tearDown() {
        dataStoreScope.coroutineContext.cancel()
        if (preferencesFile.exists()) {
            preferencesFile.delete()
        }
    }

    @Test
    fun victoryFlow_updatesStatsAndHistory() = runBlocking {
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        waitUntil { viewModel.gameState.value.board != null }
        solveBoardUsingCorrectValues(viewModel)

        waitUntil {
            viewModel.gameState.value.isGameOver &&
                    viewModel.gameState.value.isVictory &&
                    fakeGameRecordDao.insertedRecords.isNotEmpty()
        }

        val state = viewModel.gameState.value
        val record = fakeGameRecordDao.insertedRecords.first()

        assertTrue(record.isVictory)
        assertEquals(state.score, record.score)
        assertEquals(state.currentSize, record.size)
        assertEquals(state.currentDifficulty.name, record.difficulty)

        // Wait for coins update to reflect in repo
        waitUntil { repository.coinsFlow.first() > 100 }

        val coins = repository.coinsFlow.first()
        assertTrue("Coins should be increased, was $coins", coins > 100)
    }

    @Test
    fun defeatFlow_byMistakes_endsGameAndResetsStreak() = runBlocking {
        val viewModel = GameViewModel(repository, fakeGameRecordDao)
        waitUntil { viewModel.gameState.value.board != null }

        // Start with some streak
        viewModel.setPreviewState(viewModel.gameState.value.copy(streak = 5))

        // Make mistakes until game over
        repeat(viewModel.gameState.value.maxMistakes) {
            makeOneMistake(viewModel)
        }

        waitUntil {
            viewModel.gameState.value.isGameOver && fakeGameRecordDao.insertedRecords.isNotEmpty()
        }

        val state = viewModel.gameState.value
        assertFalse(state.isVictory)
        assertEquals(0, state.streak)

        val lastRecord = fakeGameRecordDao.insertedRecords.firstOrNull()
        assertNotNull(lastRecord)
        assertFalse(lastRecord!!.isVictory)
    }

    @Test
    fun defeatFlow_byTimeout_endsGame() = runBlocking {
        val viewModel = GameViewModel(repository, fakeGameRecordDao)
        waitUntil { viewModel.gameState.value.board != null }

        // Force time to 1 second
        viewModel.setPreviewState(viewModel.gameState.value.copy(timeRemaining = 1))

        // Wait for timer to tick down
        waitUntil(timeoutMs = 3000) { viewModel.gameState.value.isGameOver }

        assertTrue(viewModel.gameState.value.isGameOver)
        assertFalse(viewModel.gameState.value.isVictory)
        assertEquals(0, viewModel.gameState.value.timeRemaining)
    }

    @Test
    fun useHintFlow_deductsCoinsAndUpdatesBoard() = runBlocking {
        repository.updateCoins(500) // Ensure enough coins
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        // Wait for coins to be loaded into VM
        waitUntil { viewModel.coins.value >= 600 } // 100 initial + 500 added

        waitUntil { viewModel.gameState.value.board != null }

        val initialCoins = viewModel.coins.value
        val hintCost = 30

        viewModel.useHint()

        waitUntil { viewModel.coins.value == initialCoins - hintCost }

        // Find a cell that was filled by hint (should match its correct value and not be error)
        val cells = viewModel.gameState.value.board?.cells?.flatten() ?: emptyList()
        assertTrue(cells.any { !it.isGiven && it.value == it.correctValue && it.value != 0 })
    }

    @Test
    fun undoMistakeFlow_deductsCoinsAndDecrementsMistakes() = runBlocking {
        repository.updateCoins(500)
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        waitUntil { viewModel.coins.value >= 600 }
        waitUntil { viewModel.gameState.value.board != null }

        makeOneMistake(viewModel)
        assertEquals(1, viewModel.gameState.value.mistakes)

        val initialCoins = viewModel.coins.value
        val undoCost = 15

        viewModel.undoMistake()

        waitUntil { viewModel.coins.value == initialCoins - undoCost }
        assertEquals(0, viewModel.gameState.value.mistakes)
    }

    @Test
    fun undoMistakeFlow_withoutErrorCell_doesNotChargeCoins() = runBlocking {
        repository.updateCoins(500)
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        waitUntil { viewModel.coins.value >= 600 }
        waitUntil { viewModel.gameState.value.board != null }

        val current = viewModel.gameState.value
        viewModel.setPreviewState(current.copy(mistakes = 1))

        val initialCoins = viewModel.coins.value
        viewModel.undoMistake()
        delay(200)

        assertEquals(initialCoins, viewModel.coins.value)
        assertEquals(1, viewModel.gameState.value.mistakes)
    }

    @Test
    fun addTimeFlow_deductsCoinsAndIncreasesTime() = runBlocking {
        repository.updateCoins(500)
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        waitUntil { viewModel.coins.value >= 600 }
        waitUntil { viewModel.gameState.value.board != null }

        val initialTime = viewModel.gameState.value.timeRemaining
        val initialCoins = viewModel.coins.value
        val cost = 20
        val bonus = 30

        viewModel.addTime()

        waitUntil { viewModel.coins.value == initialCoins - cost }
        assertEquals(initialTime + bonus, viewModel.gameState.value.timeRemaining)
    }

    @Test
    fun nextLevelFlow_appliesDomainTimeBonus() = runBlocking {
        val viewModel = GameViewModel(repository, fakeGameRecordDao)

        waitUntil { viewModel.gameState.value.board != null }

        viewModel.setPreviewState(
            viewModel.gameState.value.copy(
                timeRemaining = 10,
                currentSize = 4,
                currentDifficulty = Difficulty.EASY,
                isGameOver = false,
                isVictory = false
            )
        )

        viewModel.nextLevel()

        waitUntil { viewModel.gameState.value.board != null }

        val timeRemaining = viewModel.gameState.value.timeRemaining
        assertTrue("Expected time bonus to be applied, but was $timeRemaining", timeRemaining >= 35)
        assertEquals(4, viewModel.gameState.value.currentSize)
        assertEquals(Difficulty.EASY, viewModel.gameState.value.currentDifficulty)
    }

    private suspend fun waitUntil(timeoutMs: Long = 8_000, predicate: suspend () -> Boolean) {
        withTimeout(timeoutMs) {
            while (!predicate()) {
                delay(50)
            }
        }
    }

    private fun solveBoardUsingCorrectValues(viewModel: GameViewModel) {
        while (true) {
            val board = viewModel.gameState.value.board ?: return
            val nextCell =
                board.cells.flatten().firstOrNull { !it.isGiven && it.value != it.correctValue }
                    ?: return

            viewModel.selectCell(nextCell.row, nextCell.col)
            viewModel.inputNumber(nextCell.correctValue)

            if (viewModel.gameState.value.isGameOver) return
        }
    }

    private fun makeOneMistake(viewModel: GameViewModel) {
        val board = viewModel.gameState.value.board ?: return
        val nextCell = board.cells.flatten().firstOrNull { !it.isGiven } ?: return
        val wrongValue = if (nextCell.correctValue == 1) 2 else 1

        viewModel.selectCell(nextCell.row, nextCell.col)
        viewModel.inputNumber(wrongValue)
    }

    private class InMemoryGameRecordDao : GameRecordDao {
        private val recordsFlow = MutableStateFlow<List<GameRecord>>(emptyList())

        val insertedRecords: List<GameRecord>
            get() = recordsFlow.value

        override fun getRecentRecords(): Flow<List<GameRecord>> = recordsFlow

        override suspend fun insertRecord(record: GameRecord) {
            val withGeneratedId = record.copy(id = recordsFlow.value.size + 1)
            recordsFlow.value = listOf(withGeneratedId) + recordsFlow.value
        }

        override fun getHighScore(): Flow<Int?> = recordsFlow.map { records ->
            records.maxOfOrNull { it.score }
        }
    }
}
