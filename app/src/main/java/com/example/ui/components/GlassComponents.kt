package com.example.ui.components

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
import com.example.ui.theme.GlassAccentDanger
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassAccentWarning
import com.example.ui.theme.GlassBackgroundBottom
import com.example.ui.theme.GlassBackgroundMid
import com.example.ui.theme.GlassBackgroundTop
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassSecondary
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMuted

/**
 * Full-screen luminous Light Glassmorphism background with soft pastel mesh orbs
 */
@Composable
fun LightGlassMeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassBackgroundTop,
                        GlassBackgroundMid,
                        GlassBackgroundBottom
                    )
                )
            )
            .drawBehind {
                // Top-right soft cyan glass glow orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3838BDF8),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.88f, size.height * 0.12f),
                        radius = size.width * 0.65f
                    ),
                    center = Offset(size.width * 0.88f, size.height * 0.12f),
                    radius = size.width * 0.65f
                )
                // Center-left mint/sky glass glow orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2E10B981),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.12f, size.height * 0.48f),
                        radius = size.width * 0.55f
                    ),
                    center = Offset(size.width * 0.12f, size.height * 0.48f),
                    radius = size.width * 0.55f
                )
                // Bottom-right soft cobalt glow orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x286366F1),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.82f, size.height * 0.85f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 0.82f, size.height * 0.85f),
                    radius = size.width * 0.6f
                )
            }
    ) {
        content()
    }
}

/**
 * Translucent Frosted White Glass Card with specular border highlight and subtle shadow
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevated: Boolean = false,
    accentBorderColor: Color? = null,
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (accentBorderColor != null) {
        Brush.linearGradient(
            colors = listOf(
                accentBorderColor.copy(alpha = 0.65f),
                GlassBorderWhite,
                accentBorderColor.copy(alpha = 0.35f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White,
                Color(0xB3FFFFFF),
                Color(0x4D0284C7)
            )
        )
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (elevated) 10.dp else 4.dp,
                shape = shape,
                ambientColor = Color(0x1A0284C7),
                spotColor = Color(0x240284C7)
            )
            .clip(shape)
            .border(width = 1.2.dp, brush = borderBrush, shape = shape),
        color = if (elevated) GlassSurfaceElevated else GlassSurfaceWhite,
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
 * Clickable Variable Tag Chips (e.g. {buyer}, {order_id}, {lot}, {price}, {stars}, {hours_wait})
 */
@Composable
fun TemplateVariableChipsRow(
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
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onInsertVariable(tag) },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE0F2FE),
                border = BorderStroke(1.dp, Color(0xFF7DD3FC))
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
                        color = GlassPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• $label",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 11.sp,
                        color = TextSlateDark
                    )
                }
            }
        }
    }
}

/**
 * Status Pill Badge
 */
@Composable
fun GlassStatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}

/**
 * Metric Box inside Glass Cards
 */
@Composable
fun GlassMetricTile(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color = GlassPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xD9FFFFFF),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 11.sp,
                color = TextSlateMuted
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
                color = TextSlateMuted
            )
        }
    }
}
