package com.mariolos27.gamerdex.presentation.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariolos27.gamerdex.R
import com.mariolos27.gamerdex.presentation.navigation.Screen

@Composable
fun GamerBottomNavigation(
    currentRoute: String,
    onNavigateToHome: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    isUserAuthenticated: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        color = Color(0xFFAAAAAA).copy(alpha = 0.05f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E2128).copy(alpha = 0.9f),
                            Color(0xFF0F1115).copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    iconRes = R.drawable.ic_dashboard,
                    isActive = currentRoute == Screen.Dashboard.route,
                    onClick = onNavigateToHome
                )
                BottomNavItem(
                    label = "Library",
                    iconRes = R.drawable.ic_gaming_controller,
                    isActive = currentRoute == Screen.Library.route || currentRoute == Screen.Search.route,
                    onClick = onNavigateToLibrary
                )
                BottomNavItem(
                    label = if (isUserAuthenticated) "Profile" else "Login",
                    iconRes = R.drawable.ic_profile,
                    isActive = currentRoute == Screen.Profile.route,
                    onClick = onNavigateToProfile
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    iconRes: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isActive) Color.White else Color(0xFF8B949E)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isActive) Color.White else Color(0xFF8B949E),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
