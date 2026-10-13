package local.sudoku.engine.domain.model

interface BoardGenerator {
    fun generateBoard(size: Int, difficulty: Difficulty): Board
}
