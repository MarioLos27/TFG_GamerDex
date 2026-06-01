package com.mariolos27.gamerdex.presentation.screens.library.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.borderTop(
    width: Dp = 1.dp,
    color: Color = Color.Black
) = this.then(
    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = width.toPx()
        )
    }
)

