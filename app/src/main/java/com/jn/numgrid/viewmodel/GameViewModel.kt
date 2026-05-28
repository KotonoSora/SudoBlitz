package com.jn.numgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jn.numgrid.application.game.InputNumberUseCase
import com.jn.numgrid.application.game.NextLevelUseCase
import com.jn.numgrid.application.game.StartNewGameUseCase
import com.jn.numgrid.application.game.UndoMistakeUseCase
import com.jn.numgrid.application.game.UseHintUseCase
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.game.Board
import com.jn.numgrid.domain.game.Cell
import com.jn.numgrid.domain.game.CoinRewardPolicy
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.GameSession
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
    private val preferencesRepository: UserPreferencesRepository,
    private val gameRecordDao: GameRecordDao,
    private val startNewGameUseCase: StartNewGameUseCase = StartNewGameUseCase(),
    private val nextLevelUseCase: NextLevelUseCase = NextLevelUseCase(),
    private val inputNumberUseCase: InputNumberUseCase = InputNumberUseCase(),
    private val useHintUseCase: UseHintUseCase = UseHintUseCase(),
    private val undoMistakeUseCase: UndoMistakeUseCase = UndoMistakeUseCase()
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val coins: StateFlow<Int> = preferencesRepository.coinsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )


    private var timerJob: Job? = null

    init {
        startNewGame(4, Difficulty.EASY)
    }

    fun setPreviewState(state: GameState) {
        _gameState.value = state
    }

    fun startNewGame(size: Int, difficulty: Difficulty) {
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

        _gameState.update {
            it.copy(
                isGameOver = true,
                isVictory = victory,
                streak = if (victory) it.streak + 1 else 0
            )
        }

        viewModelScope.launch {
            val state = _gameState.value

            gameRecordDao.insertRecord(
                GameRecord(
                    score = state.score,
                    difficulty = state.currentDifficulty.name,
                    size = state.currentSize,
                    isVictory = victory
                )
            )

            if (victory) {
                val reward = CoinRewardPolicy.forVictory(
                    timeRemaining = state.timeRemaining,
                    boardSize = state.currentSize
                )

                _gameState.update {
                    it.copy(
                        coinsEarned = reward.totalCoins,
                        coinDetails = reward.details
                    )
                }
                preferencesRepository.updateCoins(reward.totalCoins)
                preferencesRepository.updateBestStreak(state.streak)
            }
            preferencesRepository.updateHighScore(state.score)
        }
    }

    fun addTime() {
        val cost = 20
        if (coins.value >= cost) {
            viewModelScope.launch {
                preferencesRepository.updateCoins(-cost)
                _gameState.update { it.copy(timeRemaining = it.timeRemaining + 30) }
            }
        }
    }

    fun useHint() {
        val cost = 30
        val state = _gameState.value
        val session = state.toSession() ?: return

        if (coins.value >= cost && !state.isGameOver) {
            viewModelScope.launch {
                val nextSession = useHintUseCase.execute(session)
                if (nextSession == session) return@launch

                preferencesRepository.updateCoins(-cost)
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
        val cost = 15
        val state = _gameState.value
        val session = state.toSession() ?: return

        if (coins.value >= cost && !state.isGameOver && state.mistakes > 0) {
            viewModelScope.launch {
                val nextSession = undoMistakeUseCase.execute(session)
                if (nextSession == session) return@launch

                preferencesRepository.updateCoins(-cost)
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

    companion object {
        fun provideFactory(
            repository: UserPreferencesRepository, gameRecordDao: GameRecordDao
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                    return GameViewModel(repository, gameRecordDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
