package local.sudoku.engine.domain.model

data class Cell(
    val row: Int,
    val col: Int,
    val value: Int = 0,
    val correctValue: Int = 0,
    val isGiven: Boolean = false,
    val isError: Boolean = false
) {
    val isEmpty: Boolean get() = value == 0
}
