package com.jn.numgrid.domain.game

interface BoardGenerator {
    fun generateBoard(size: Int, difficulty: Difficulty): Board
}
