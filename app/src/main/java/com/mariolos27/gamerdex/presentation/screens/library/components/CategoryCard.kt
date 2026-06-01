package com.mariolos27.gamerdex.presentation.screens.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariolos27.gamerdex.presentation.screens.library.model.LibraryCategory

@Composable
fun CategoryCard(
    category: LibraryCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) category.color.copy(alpha = 0.1f)
                else Color(0xFF1E2128).copy(alpha = 0.8f)
            ),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            // Icon Container
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = category.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                color = Color.Transparent
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = category.icon),
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Category Info
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = category.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8B949E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.count.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) category.color else Color.White
                )
            }
        }
    }
}

