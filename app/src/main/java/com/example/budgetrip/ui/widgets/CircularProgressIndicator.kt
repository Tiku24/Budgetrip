package com.example.budgetrip.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SlowCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color,
    strokeWidth: Dp = 8.dp,
    speedMultiplier: Float = 0.2f // Lower is slower (default = 1.0f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1000 / speedMultiplier).toInt(), // Slower rotation
                easing = LinearEasing
            )
        ),
        label = "angle"
    )

    Canvas(
        modifier = modifier
            .size(50.dp)
            .graphicsLayer {
                rotationZ = angle.value
            }
    ) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 270f, // semi-circle style
            useCenter = false,
            style = stroke
        )
    }
}

@Composable
fun ConcentricCircleLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(22.dp)
            .graphicsLayer { rotationZ = rotation },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(22.dp)) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.3f),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.6f),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Canvas(modifier = Modifier.size(3.dp)) {
            drawCircle(
                color = Color.Gray,
                style = Fill
            )
        }
    }
}