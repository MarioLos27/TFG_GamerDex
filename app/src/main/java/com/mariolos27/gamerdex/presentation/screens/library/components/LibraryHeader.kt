package com.mariolos27.gamerdex.presentation.screens.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariolos27.gamerdex.presentation.theme.DarkBackground

@Composable
fun LibraryHeader(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Title Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Game Library",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Manage and track your collection",
                    fontSize = 16.sp,
                    color = Color(0xFF8B949E)
                )
            }

            // Search Field
            TextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChanged(it)
                    if (it.isNotEmpty()) {
                        onSearch()
                    }
                },
                placeholder = {
                    Text(
                        "Search library...",
                        color = Color(0xFF8B949E),
                        fontSize = 18.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                leadingIcon = {
                    Text("🔍", fontSize = 20.sp, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E2128),
                    unfocusedContainerColor = Color(0xFF1E2128),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    color = Color.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

