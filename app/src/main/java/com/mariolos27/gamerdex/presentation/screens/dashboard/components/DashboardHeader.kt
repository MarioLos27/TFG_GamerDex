package com.mariolos27.gamerdex.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Discover",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "Find your next adventure",
            fontSize = 16.sp,
            color = Color(0xFF8B949E),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
