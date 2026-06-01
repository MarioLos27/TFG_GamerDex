package com.mariolos27.gamerdex.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.presentation.theme.DarkSurface
import com.mariolos27.gamerdex.presentation.theme.DarkSurfaceVariant
import com.mariolos27.gamerdex.presentation.theme.GamerCyan
import com.mariolos27.gamerdex.presentation.theme.GamerPink
import com.mariolos27.gamerdex.presentation.theme.GamerPurple
import com.mariolos27.gamerdex.presentation.theme.GradientEnd
import com.mariolos27.gamerdex.presentation.theme.GradientStart
import com.mariolos27.gamerdex.presentation.theme.TextPrimary
import com.mariolos27.gamerdex.presentation.theme.TextSecondary
import com.mariolos27.gamerdex.presentation.theme.TextTertiary

/**
 * Tarjeta de juego personalizada con efecto hover y animaciones
 */
@Composable
fun GameCard(
    game: Game,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 12.dp else 4.dp,
        label = "GameCardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = GamerPurple.copy(alpha = 0.3f),
                spotColor = GamerPurple.copy(alpha = 0.4f)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isHovered = !isHovered
                onClick?.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portada con overlay gradiente
            GameCardCover(game = game)

            // Contenido del texto
            GameCardContent(game = game)
        }
    }
}

/**
 * Portada del juego con placeholder animado
 */
@Composable
private fun GameCardCover(
    game: Game,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(110.dp, 110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GamerPurple.copy(alpha = 0.3f),
                        GamerCyan.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        if (game.coverUrl != null && game.coverUrl.isNotEmpty()) {
            AsyncImage(
                model = game.coverUrl,
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay gradiente para mejor legibilidad
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        } else {
            // Placeholder con icono
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎮",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    }
}

/**
 * Contenido de texto de la tarjeta
 */
@Composable
private fun GameCardContent(
    game: Game,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Título del juego
        Text(
            text = game.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Información adicional
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ID o número
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = DarkSurfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${game.id}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GamerCyan
                    )
                )
            }

            // Badge interactivo
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GamerPurple.copy(alpha = 0.8f), GamerPink.copy(alpha = 0.8f))
                        )
                    )
                    .padding(4.dp, 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ Tap for more details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextPrimary
                    )
                )
            }
        }
    }
}

/**
 * Loading skeleton para la tarjeta
 */
@Composable
fun GameCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant.copy(alpha = 0.5f))
    )
}




