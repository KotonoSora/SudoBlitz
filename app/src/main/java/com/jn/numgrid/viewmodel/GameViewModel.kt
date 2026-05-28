package com.jn.numgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.engine.SudokuEngine
import com.jn.numgrid.model.Board
import com.jn.numgrid.model.Cell
import com.jn.numgrid.model.Difficulty
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
    val coinDetails: String = ""
)

class GameViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val gameRecordDao: GameRecordDao
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val coins: StateFlow<Int> = preferencesRepository.coinsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val highScore: StateFlow<Int> = preferencesRepository.highScoreFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val bestStreak: StateFlow<Int> = preferencesRepository.bestStreakFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
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
        val startingTime = when (size) {
            4 -> 60
            6 -> 180
            9 -> 300
            else -> 180
        }

        // Reset state immediately (except for scores/streak if continuing, but here it's a new game)
        _gameState.update {
            it.copy(
                isGameOver = false,
                isVictory = false,
                board = null,
                score = 0,
                comboMultiplier = 0,
                mistakes = 0,
                streak = 0
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val board = SudokuEngine.generateBoard(size, difficulty)

            _gameState.update {
                it.copy(
                    board = board,
                    selectedCell = null,
                    timeRemaining = startingTime,
                    currentDifficulty = difficulty,
                    currentSize = size
                )
            }
            startTimer()
        }
    }

    fun nextLevel() {
        val state = _gameState.value
        val newSize = state.currentSize
        val newDifficulty = state.currentDifficulty

        val startingTime = when (newSize) {
            4 -> 60
            6 -> 180
            9 -> 300
            else -> 180
        }

        // Reset state immediately to clear game over flags and old board
        _gameState.update {
            it.copy(
                isGameOver = false,
                isVictory = false,
                board = null,
                mistakes = 0,
                comboMultiplier = 0
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val board = SudokuEngine.generateBoard(newSize, newDifficulty)
            _gameState.update {
                it.copy(
                    board = board,
                    selectedCell = null,
                    timeRemaining = it.timeRemaining + startingTime / 2,
                    currentDifficulty = newDifficulty,
                    currentSize = newSize
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
                _gameState.update {
                    if (it.timeRemaining > 0 && !it.isGameOver) {
                        it.copy(timeRemaining = it.timeRemaining - 1)
                    } else if (it.timeRemaining <= 0 && !it.isGameOver) {
                        endGame(false)
                        it
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun selectCell(row: Int, col: Int) {
        val state = _gameState.value
        if (state.isGameOver || state.board == null) return

        val cell = state.board.getCell(row, col)
        if (!cell.isGiven) {
            _gameState.update { it.copy(selectedCell = cell) }
        }
    }

    fun inputNumber(number: Int) {
        val state = _gameState.value
        if (state.isGameOver || state.board == null) return
        val cell = state.selectedCell ?: return

        if (cell.isGiven) return

        val isCorrect = number == cell.correctValue

        val newBoard = state.board.updateCell(cell.row, cell.col) {
            it.copy(value = number, isError = !isCorrect)
        }

        if (isCorrect) {
            _gameState.update {
                val nextMultiplier = minOf(it.comboMultiplier + 1, 5)
                val points = 10 * nextMultiplier
                it.copy(
                    board = newBoard,
                    score = it.score + points,
                    comboMultiplier = nextMultiplier
                )
            }
            checkVictory(newBoard)
        } else {
            _gameState.update {
                it.copy(
                    board = newBoard, comboMultiplier = 0, mistakes = it.mistakes + 1
                )
            }
            if (_gameState.value.mistakes >= _gameState.value.maxMistakes) {
                endGame(false)
            }
        }
    }

    private fun checkVictory(board: Board) {
        if (board.isSolved()) {
            endGame(true)
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

            // Save to history
            gameRecordDao.insertRecord(
                GameRecord(
                    score = state.score,
                    difficulty = state.currentDifficulty.name,
                    size = state.currentSize,
                    isVictory = victory
                )
            )

            if (victory) {
                val base = 10
                val timeBonus = state.timeRemaining / 10
                val sizeBonus = state.currentSize
                val coinsEarned = base + timeBonus + sizeBonus
                val details = "Base: $base, Time: +$timeBonus, Size: +$sizeBonus"
                
                _gameState.update { it.copy(coinsEarned = coinsEarned, coinDetails = details) }
                preferencesRepository.updateCoins(coinsEarned)
                preferencesRepository.updateBestStreak(state.streak + 1)
            } else {
                _gameState.update { it.copy(streak = 0) }
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
        val board = state.board ?: return

        if (coins.value >= cost && !state.isGameOver) {
            viewModelScope.launch {
                val targetCell = board.cells.flatten().find { it.isEmpty || it.isError }
                if (targetCell != null) {
                    preferencesRepository.updateCoins(-cost)
                    val newBoard = board.updateCell(targetCell.row, targetCell.col) {
                        it.copy(value = it.correctValue, isError = false)
                    }
                    _gameState.update { it.copy(board = newBoard) }
                    checkVictory(newBoard)
                }
            }
        }
    }

    fun undoMistake() {
        val cost = 15
        val state = _gameState.value
        val board = state.board ?: return

        if (coins.value >= cost && !state.isGameOver && state.mistakes > 0) {
            viewModelScope.launch {
                val targetCell = board.cells.flatten().lastOrNull { it.isError }
                if (targetCell != null) {
                    preferencesRepository.updateCoins(-cost)
                    val newBoard = board.updateCell(targetCell.row, targetCell.col) {
                        it.copy(value = 0, isError = false)
                    }
                    _gameState.update {
                        it.copy(
                            board = newBoard, mistakes = it.mistakes - 1
                        )
                    }
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
