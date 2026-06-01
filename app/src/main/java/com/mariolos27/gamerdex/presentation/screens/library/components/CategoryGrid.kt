package com.mariolos27.gamerdex.presentation.screens.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mariolos27.gamerdex.presentation.screens.library.model.LibraryCategory

@Composable
fun CategoryGrid(
    categories: List<LibraryCategory>,
    selectedCategory: Int,
    onCategoryClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEachIndexed { index, category ->
            CategoryCard(
                category = category,
                isSelected = selectedCategory == index,
                onClick = { onCategoryClick(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
