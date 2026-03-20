package com.kotonosora.sudoblitz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.model.Board
import com.kotonosora.sudoblitz.model.Cell
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.ErrorRed
import com.kotonosora.sudoblitz.ui.theme.NeonBlue
import com.kotonosora.sudoblitz.ui.theme.NeonCyan
import com.kotonosora.sudoblitz.ui.theme.NeonGreen
import com.kotonosora.sudoblitz.ui.theme.NeonMagenta

@Composable
fun SudokuGrid(
    board: Board,
    selectedCell: Cell?,
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val outerBorderColor = NeonCyan

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(3.dp, outerBorderColor, RoundedCornerShape(8.dp))
            .background(DarkBackground)
    ) {
        board.cells.forEachIndexed { r, row ->
            Row {
                row.forEachIndexed { c, cell ->
                    SudokuCellView(
                        cell = cell,
                        isSelected = selectedCell?.row == r && selectedCell?.col == c,
                        boardSize = board.size,
                        regionRows = board.regionRows,
                        regionCols = board.regionCols,
                        onCellSelected = { onCellSelected(r, c) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuCellView(
    cell: Cell,
    isSelected: Boolean,
    boardSize: Int,
    regionRows: Int,
    regionCols: Int,
    onCellSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thinBorder = NeonBlue.copy(alpha = 0.5f)
    val thickBorder = NeonMagenta

    val bgColor = when {
        isSelected -> NeonCyan.copy(alpha = 0.2f)
        cell.isError -> ErrorRed.copy(alpha = 0.2f)
        cell.isGiven -> DarkBackground.copy(alpha = 0.9f)
        else -> DarkBackground
    }

    val textColor = when {
        cell.isError -> ErrorRed
        cell.isGiven -> Color.White
        else -> NeonGreen
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .background(bgColor)
            .clickable(onClick = onCellSelected)
            .drawBehind {
                val strokeThin = 1.dp.toPx()
                val strokeThick = 2.5.dp.toPx()

                if (cell.row != boardSize - 1) {
                    val isThickBottom = (cell.row + 1) % regionRows == 0
                    drawLine(
                        color = if (isThickBottom) thickBorder else thinBorder,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = if (isThickBottom) strokeThick else strokeThin
                    )
                }

                if (cell.col != boardSize - 1) {
                    val isThickRight = (cell.col + 1) % regionCols == 0
                    drawLine(
                        color = if (isThickRight) thickBorder else thinBorder,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = if (isThickRight) strokeThick else strokeThin
                    )
                }
            }
    ) {
        if (!cell.isEmpty) {
            val fSize = when (boardSize) {
                4 -> 32
                6 -> 24
                else -> 18 // 9x9 needs slightly smaller text
            }
            NeonText(
                text = cell.value.toString(),
                fontSize = fSize,
                color = textColor
            )
        }
    }
}
