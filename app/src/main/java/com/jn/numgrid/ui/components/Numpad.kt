package com.jn.numgrid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jn.numgrid.ui.theme.NeonCyan

@Composable
fun Numpad(
    size: Int, onNumberSelected: (Int) -> Unit, modifier: Modifier = Modifier
) {
    if (size > 6) {
        Column(
            modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..5) {
                    NumpadButton(
                        number = i,
                        onNumberSelected = onNumberSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 6..size) {
                    NumpadButton(
                        number = i,
                        onNumberSelected = onNumberSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add a spacer to balance the remaining space if size is 9 (so the buttons aren't wider than the top row)
                if (size == 9) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..size) {
                NumpadButton(
                    number = i, onNumberSelected = onNumberSelected, modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NumpadButton(
    number: Int, onNumberSelected: (Int) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { onNumberSelected(number) },
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(2.dp, NeonCyan),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = NeonCyan.copy(alpha = 0.05f),
            contentColor = NeonCyan
        ),
        modifier = modifier
            .aspectRatio(0.85f)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            NeonText(
                text = number.toString(), color = NeonCyan, fontSize = 28
            )
        }
    }
}
