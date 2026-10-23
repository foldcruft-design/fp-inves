package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBackgroundElevated
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite

/**
 * Animated Ambient Dark Background with subtle moving cyber orbs
 */
@Composable
fun DeepDarkCyberBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_glow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .drawBehind {
                // Top-right cyan neon ambient orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3300F0FF),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f + glowOffset, size.height * 0.12f),
                        radius = size.width * 0.7f
                    ),
                    center = Offset(size.width * 0.85f + glowOffset, size.height * 0.12f),
                    radius = size.width * 0.7f
                )
                // Mid-left purple ambient orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x24A855F7),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.15f - glowOffset, size.height * 0.55f),
                        radius = size.width * 0.65f
                    ),
                    center = Offset(size.width * 0.15f - glowOffset, size.height * 0.55f),
                    radius = size.width * 0.65f
                )
                // Bottom royal blue glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x283B82F6),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.95f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.95f),
                    radius = size.width * 0.8f
                )
            }
    ) {
        content()
    }
}

/**
 * Pitch Dark Glass Card with Neon Border and smooth elevation
 */
@Composable
fun DarkNeonGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    elevated: Boolean = false,
    accentBorderColor: Color? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (accentBorderColor != null) {
        Brush.linearGradient(
            colors = listOf(
                accentBorderColor.copy(alpha = 0.8f),
                DarkBorder,
                accentBorderColor.copy(alpha = 0.3f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x4038BDF8),
                Color(0x1A38BDF8),
                Color(0x0A38BDF8)
            )
        )
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (elevated) 12.dp else 4.dp,
                shape = shape,
                ambientColor = Color(0x3300F0FF),
                spotColor = Color(0x4000F0FF)
            )
            .clip(shape)
            .border(width = 1.dp, brush = borderBrush, shape = shape),
        color = if (elevated) DarkSurfaceElevated else DarkSurfaceCard,
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Google Multi-Color G Icon Component
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(20.dp)) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoogleRed))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoogleYellow))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoogleGreen))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoogleBlue))
        }
    }
}

/**
 * Template Variable Chips Row
 */
@Composable
fun DarkTemplateVariableChipsRow(
    variables: List<Pair<String, String>>,
    onInsertVariable: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        variables.forEach { (tag, label) ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onInsertVariable(tag) },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F1A28),
                border = BorderStroke(1.dp, Color(0xFF1E3A5F))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tag,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• $label",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }
            }
        }
    }
}

/**
 * Neon Status Badge
 */
@Composable
fun DarkNeonStatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = color
            )
        }
    }
}

/**
 * Metric Card inside Dark Cyber Dashboard
 */
@Composable
fun DarkMetricTile(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0A1017),
        border = BorderStroke(1.dp, Color(0x3338BDF8))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 11.sp,
                color = TextDim
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = TextLight
            )
        }
    }
}
