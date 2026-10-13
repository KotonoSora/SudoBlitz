package local.sudoku.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.sudoku.engine.application.InputNumberUseCase
import local.sudoku.engine.application.NextLevelUseCase
import local.sudoku.engine.application.StartNewGameUseCase
import local.sudoku.engine.application.UndoMistakeUseCase
import local.sudoku.engine.application.UseHintUseCase
import local.sudoku.engine.application.UsePowerUpUseCase
import local.sudoku.engine.domain.model.Board
import local.sudoku.engine.domain.model.Cell
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.engine.domain.model.GameSession
import local.sudoku.engine.domain.model.PowerUpType
import local.sudoku.engine.domain.model.RunEndCause
import local.sudoku.engine.domain.model.RunRecord
import local.sudoku.engine.domain.service.RewardPolicy
import local.sudoku.shop.domain.service.PowerUps
import local.sudoku.ui.application.RecordGameFinishedUseCase

data class GameState(
    val board: Board? = null,
    val selectedCell: Cell? = null,
    val timeRemaining: Int = 180, // 3 minutes
    val score: Int = 0,
    val comboMultiplier: Int = 0,
    val mistakes: Int = 0,
    val maxMistakes: Int = 3,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val currentDifficulty: Difficulty = Difficulty.EASY,
    val currentSize: Int = 4,
    val streak: Int = 0,
    val coinsEarned: Int = 0,
    val coinDetails: String = "",
) {
    fun toSession(): GameSession? {
        val b = board ?: return null
        return GameSession(
            board = b,
            difficulty = currentDifficulty,
            score = score,
            mistakes = mistakes,
            maxMistakes = maxMistakes,
            comboMultiplier = comboMultiplier,
            isGameOver = isGameOver,
            isVictory = isVictory
        )
    }

    companion object {
        fun fromSession(
            session: GameSession,
            timeRemaining: Int,
            streak: Int,
            selectedCell: Cell? = null,
            coinsEarned: Int = 0,
            coinDetails: String = ""
        ): GameState {
            return GameState(
                board = session.board,
                selectedCell = selectedCell,
                timeRemaining = timeRemaining,
                score = session.score,
                comboMultiplier = session.comboMultiplier,
                mistakes = session.mistakes,
                maxMistakes = session.maxMistakes,
                isGameOver = session.isGameOver,
                isVictory = session.isVictory,
                currentDifficulty = session.difficulty,
                currentSize = session.board.size,
                streak = streak,
                coinsEarned = coinsEarned,
                coinDetails = coinDetails
            )
        }
    }
}

class GameViewModel(
    private val coinAccount: CoinAccount,
    private val inventoryStore: InventoryStore,
    private val recordGameFinishedUseCase: RecordGameFinishedUseCase,
    private val usePowerUpUseCase: UsePowerUpUseCase,
    private val startNewGameUseCase: StartNewGameUseCase = StartNewGameUseCase(),
    private val nextLevelUseCase: NextLevelUseCase = NextLevelUseCase(),
    private val inputNumberUseCase: InputNumberUseCase = InputNumberUseCase(),
    private val useHintUseCase: UseHintUseCase = UseHintUseCase(),
    private val undoMistakeUseCase: UndoMistakeUseCase = UndoMistakeUseCase()
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val coins: StateFlow<Int> = coinAccount.coinsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    val extraTimeCount: StateFlow<Int> =
        inventoryStore.inventoryCountFlow(PowerUpType.EXTRA_TIME.id).stateIn(
            viewModelScope, SharingStarted.Eagerly, 0
        )
    val hintCount: StateFlow<Int> =
        inventoryStore.inventoryCountFlow(PowerUpType.HINT.id).stateIn(
            viewModelScope, SharingStarted.Eagerly, 0
        )
    val undoCount: StateFlow<Int> =
        inventoryStore.inventoryCountFlow(PowerUpType.UNDO.id).stateIn(
            viewModelScope, SharingStarted.Eagerly, 0
        )

    private var timerJob: Job? = null
    private var sessionStartedAt: Long = System.currentTimeMillis()

    // Power-up uses in the current run (recorded into the run row).
    private var sessionExtraTimeUses = 0
    private var sessionHintUses = 0
    private var sessionUndoUses = 0

    init {
        startNewGame(4, Difficulty.EASY)
    }

    fun setPreviewState(state: GameState) {
        _gameState.value = state
    }

    fun startNewGame(size: Int, difficulty: Difficulty) {
        resetSessionCounters()
        _gameState.update {
            it.copy(
                board = null,
                selectedCell = null,
                score = 0,
                comboMultiplier = 0,
                mistakes = 0,
                isGameOver = false,
                isVictory = false,
                streak = 0,
                coinsEarned = 0,
                coinDetails = ""
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val result = startNewGameUseCase.execute(size, difficulty)
            _gameState.update {
                GameState.fromSession(
                    session = result.session,
                    timeRemaining = result.initialTimeSeconds,
                    streak = 0
                )
            }
            startTimer()
        }
    }

    fun nextLevel() {
        val state = _gameState.value
        val currentSize = state.currentSize
        val currentDifficulty = state.currentDifficulty

        resetSessionCounters()
        _gameState.update {
            it.copy(
                board = null,
                selectedCell = null,
                score = 0,
                comboMultiplier = 0,
                mistakes = 0,
                isGameOver = false,
                isVictory = false,
                coinsEarned = 0,
                coinDetails = ""
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val result = nextLevelUseCase.execute(currentSize, currentDifficulty)
            _gameState.update {
                GameState.fromSession(
                    session = result.session,
                    timeRemaining = it.timeRemaining + result.addedTimeSeconds,
                    streak = it.streak,
                    selectedCell = null
                )
            }
            startTimer()
        }
    }

    private fun resetSessionCounters() {
        sessionStartedAt = System.currentTimeMillis()
        sessionExtraTimeUses = 0
        sessionHintUses = 0
        sessionUndoUses = 0
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _gameState.value
                if (state.isGameOver) break

                if (state.timeRemaining > 0) {
                    _gameState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
                } else {
                    endGame(victory = false)
                    break
                }
            }
        }
    }

    fun selectCell(row: Int, col: Int) {
        val state = _gameState.value
        if (state.isGameOver || (state.board == null)) return

        val cell = state.board.getCell(row, col)
        if (!cell.isGiven) {
            _gameState.update { it.copy(selectedCell = cell) }
        }
    }

    fun inputNumber(number: Int) {
        val state = _gameState.value
        val session = state.toSession() ?: return
        val cell = state.selectedCell ?: return
        if (state.isGameOver) return

        val nextSession = inputNumberUseCase.execute(session, cell.row, cell.col, number)
        _gameState.update {
            GameState.fromSession(
                session = nextSession,
                timeRemaining = it.timeRemaining,
                streak = it.streak,
                selectedCell = it.selectedCell
            )
        }

        if (nextSession.isGameOver) {
            endGame(nextSession.isVictory)
        }
    }

    private fun endGame(victory: Boolean) {
        timerJob?.cancel()
        val endedAt = System.currentTimeMillis()

        _gameState.update {
            it.copy(
                isGameOver = true,
                isVictory = victory,
                streak = if (victory) it.streak + 1 else 0
            )
        }

        viewModelScope.launch {
            val state = _gameState.value
            val reward = if (victory) {
                RewardPolicy.forVictory(
                    timeRemaining = state.timeRemaining,
                    boardSize = state.currentSize
                )
            } else null

            _gameState.update {
                it.copy(
                    coinsEarned = reward?.totalCoins ?: 0,
                    coinDetails = reward?.details ?: ""
                )
            }

            recordGameFinishedUseCase(
                RunRecord(
                    mode = state.currentDifficulty.name,
                    level = state.currentSize,
                    score = state.score,
                    reward = reward?.totalCoins ?: 0,
                    durationMs = endedAt - sessionStartedAt,
                    // Repurposed core field: mistake count (see engine RunRecord KDoc).
                    pipesPassed = state.mistakes,
                    endCause = if (victory) RunEndCause.VICTORY else RunEndCause.GAME_OVER,
                    shieldUses = sessionHintUses,
                    multiplierUses = sessionUndoUses,
                    autoPlayUses = sessionExtraTimeUses,
                    startedAt = sessionStartedAt,
                    endedAt = endedAt
                ),
                state.streak
            )
        }
    }

    fun addTime() {
        val state = _gameState.value
        if (state.isGameOver) return
        viewModelScope.launch {
            if (usePowerUpUseCase(PowerUpType.EXTRA_TIME.id)) {
                sessionExtraTimeUses++
                _gameState.update { it.copy(timeRemaining = it.timeRemaining + PowerUps.EXTRA_TIME_SECONDS) }
            }
        }
    }

    fun useHint() {
        val state = _gameState.value
        val session = state.toSession() ?: return
        if (state.isGameOver) return
        // Don't charge when the hint would be a no-op.
        if (session.board.cells.flatten().none { it.isEmpty || it.isError }) return

        viewModelScope.launch {
            if (usePowerUpUseCase(PowerUpType.HINT.id)) {
                val nextSession = useHintUseCase.execute(session)
                sessionHintUses++
                _gameState.update {
                    GameState.fromSession(
                        session = nextSession,
                        timeRemaining = it.timeRemaining,
                        streak = it.streak,
                        selectedCell = it.selectedCell
                    )
                }
                if (nextSession.isGameOver) {
                    endGame(nextSession.isVictory)
                }
            }
        }
    }

    fun undoMistake() {
        val state = _gameState.value
        val session = state.toSession() ?: return
        if (state.isGameOver || state.mistakes <= 0) return

        viewModelScope.launch {
            if (usePowerUpUseCase(PowerUpType.UNDO.id)) {
                val nextSession = undoMistakeUseCase.execute(session)
                sessionUndoUses++
                _gameState.update {
                    GameState.fromSession(
                        session = nextSession,
                        timeRemaining = it.timeRemaining,
                        streak = it.streak,
                        selectedCell = it.selectedCell
                    )
                }
            }
        }
    }
}
