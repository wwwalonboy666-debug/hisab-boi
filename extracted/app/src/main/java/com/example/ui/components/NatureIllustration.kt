package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NatureEarthTertiaryLight
import com.example.ui.theme.NatureGreenPrimaryLight

@Composable
fun PeacefulNatureHeaderCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 85.dp, max = 115.dp)
                .aspectRatio(3.2f, matchHeightConstraintsFirst = false)
        ) {
            // Background illustration
            Image(
                painter = painterResource(id = R.drawable.img_nature_growth),
                contentDescription = "Nature scenery",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.85f
            )

            // Gradient overlay for readability and peaceful tone
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun GrowingPlantVisualizer(
    progress: Float, // 0.0 to 1.0+
    modifier: Modifier = Modifier,
    stageName: String = ""
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breeze")
    val sway by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_sway"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val w = size.width
                val h = size.height
                val safeProgress = progress.coerceIn(0f, 1f)

                // Soil base
                drawArc(
                    color = Color(0xFF6D4C41),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(w * 0.2f, h * 0.72f),
                    size = Size(w * 0.6f, h * 0.24f)
                )

                // Stem
                val stemHeight = h * (0.25f + safeProgress * 0.45f)
                val stemTopY = h * 0.8f - stemHeight
                val stemColor = Color(0xFF388E3C)

                val stemPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.8f)
                    quadraticTo(
                        w * 0.5f + sway,
                        h * 0.8f - stemHeight * 0.5f,
                        w * 0.5f + (sway * 1.5f),
                        stemTopY
                    )
                }
                drawPath(
                    path = stemPath,
                    color = stemColor,
                    style = Stroke(width = 5.dp.toPx())
                )

                // Leaves depending on progress
                if (safeProgress >= 0.15f) {
                    // First leaf (left)
                    val leaf1Path = Path().apply {
                        val originX = w * 0.5f + (sway * 0.6f)
                        val originY = h * 0.75f - stemHeight * 0.35f
                        moveTo(originX, originY)
                        cubicTo(
                            originX - 18.dp.toPx(), originY - 14.dp.toPx(),
                            originX - 26.dp.toPx(), originY + 2.dp.toPx(),
                            originX, originY + 4.dp.toPx()
                        )
                    }
                    drawPath(leaf1Path, color = Color(0xFF4CAF50))
                }

                if (safeProgress >= 0.4f) {
                    // Second leaf (right)
                    val leaf2Path = Path().apply {
                        val originX = w * 0.5f + (sway * 0.9f)
                        val originY = h * 0.75f - stemHeight * 0.6f
                        moveTo(originX, originY)
                        cubicTo(
                            originX + 20.dp.toPx(), originY - 16.dp.toPx(),
                            originX + 28.dp.toPx(), originY + 2.dp.toPx(),
                            originX, originY + 5.dp.toPx()
                        )
                    }
                    drawPath(leaf2Path, color = Color(0xFF66BB6A))
                }

                if (safeProgress >= 0.75f) {
                    // Top foliage bud/leaves
                    drawCircle(
                        color = Color(0xFF81C784),
                        radius = 8.dp.toPx(),
                        center = Offset(w * 0.5f + (sway * 1.5f), stemTopY - 4.dp.toPx())
                    )
                }

                if (safeProgress >= 0.99f) {
                    // Golden blossom flower
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = 6.dp.toPx(),
                        center = Offset(w * 0.5f + (sway * 1.5f), stemTopY - 6.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFFFF9800),
                        radius = 3.dp.toPx(),
                        center = Offset(w * 0.5f + (sway * 1.5f), stemTopY - 6.dp.toPx())
                    )
                }
            }
        }

        if (stageName.isNotEmpty()) {
            Text(
                text = stageName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun EmptyStateNatureView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val w = size.width
                val h = size.height
                // Stylized serene sprout & leaf
                val leafPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.85f)
                    cubicTo(w * 0.2f, h * 0.55f, w * 0.2f, h * 0.2f, w * 0.5f, h * 0.15f)
                    cubicTo(w * 0.8f, h * 0.2f, w * 0.8f, h * 0.55f, w * 0.5f, h * 0.85f)
                }
                drawPath(leafPath, color = Color(0xFF4CAF50).copy(alpha = 0.85f))

                // Leaf spine
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(w * 0.5f, h * 0.75f),
                    end = Offset(w * 0.5f, h * 0.25f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 16.dp, end = 16.dp)
        )
    }
}
