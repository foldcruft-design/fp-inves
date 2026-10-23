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
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite

/**
 * Refined Dark Obsidian Glass Background with subtle soft frosted light reflection
 */
@Composable
fun DeepDarkCyberBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_ambient")
    val drift by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0F17),
                        DarkBackground,
                        Color(0xFF07090E)
                    )
                )
            )
            .drawBehind {
                // Soft top-right ice reflection
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1838BDF8),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f + drift, size.height * 0.1f),
                        radius = size.width * 0.65f
                    ),
                    center = Offset(size.width * 0.85f + drift, size.height * 0.1f),
                    radius = size.width * 0.65f
                )
                // Subtle bottom-left silver-blue reflection
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1294A3B8),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.15f - drift, size.height * 0.75f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 0.15f - drift, size.height * 0.75f),
                    radius = size.width * 0.6f
                )
            }
    ) {
        content()
    }
}

/**
 * Compact Dark Frosted Glass Card with thin specular white/silver edge
 */
@Composable
fun DarkNeonGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    elevated: Boolean = false,
    accentBorderColor: Color? = null,
    contentPadding: Dp = 13.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (accentBorderColor != null) {
        Brush.linearGradient(
            colors = listOf(
                accentBorderColor.copy(alpha = 0.55f),
                Color(0x26FFFFFF),
                accentBorderColor.copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x38FFFFFF),
                Color(0x14FFFFFF),
                Color(0x2438BDF8)
            )
        )
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (elevated) 8.dp else 3.dp,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(shape)
            .border(width = 0.9.dp, brush = borderBrush, shape = shape),
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
 * Pure Translucent Frosted Glass Button (Чисто стеклянная кнопка)
 */
@Composable
fun PureGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentTint: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x2EFFFFFF),
            Color(0x14FFFFFF)
        )
    )
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x66FFFFFF),
            accentTint.copy(alpha = 0.35f),
            Color(0x28FFFFFF)
        )
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(brush = glassBrush, shape = shape)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * Google 4-Dot Compact Badge
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(16.dp)) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(4.5.dp).clip(CircleShape).background(GoogleRed))
            Box(modifier = Modifier.size(4.5.dp).clip(CircleShape).background(GoogleYellow))
            Box(modifier = Modifier.size(4.5.dp).clip(CircleShape).background(GoogleGreen))
            Box(modifier = Modifier.size(4.5.dp).clip(CircleShape).background(GoogleBlue))
        }
    }
}

/**
 * Compact Template Variable Chips Row
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        variables.forEach { (tag, label) ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onInsertVariable(tag) },
                shape = RoundedCornerShape(8.dp),
                color = Color(0x1FFFFFFF),
                border = BorderStroke(0.8.dp, Color(0x33FFFFFF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tag,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 10.sp,
                        color = TextLight
                    )
                }
            }
        }
    }
}

/**
 * Compact Frosted Status Badge
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
        color = Color(0x1AFFFFFF),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextWhite
            )
        }
    }
}

/**
 * Compact Metric Tile inside Dark Glass Card (No text overflow)
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
        shape = RoundedCornerShape(13.dp),
        color = Color(0x17FFFFFF),
        border = BorderStroke(0.8.dp, Color(0x26FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextDim
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextLight
            )
        }
    }
}
