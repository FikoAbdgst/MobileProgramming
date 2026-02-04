package com.example.uasecom.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToNext: () -> Unit) {
    // Animation states
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Icon animation
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        // Rotation effect
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )

        // Fade in background
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )

        // Text fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        // Scale back to normal
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        delay(2000L)
        onNavigateToNext()
    }

    // Animated gradient colors
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f + gradientOffset * 0.1f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f + gradientOffset * 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f + gradientOffset * 0.2f)
                    )
                )
            )
            .alpha(alpha.value),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with glow effect
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Logo Glow",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale.value * 1.1f)
                        .rotate(rotation.value)
                )

                // Main icon
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .rotate(rotation.value)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name with animation
            Text(
                text = "UASEcom",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .scale(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your Premium Shopping Experience",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Loading indicator
            LoadingDots(modifier = Modifier.alpha(textAlpha.value))
        }
    }
}

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val dots = 3
    val animationDelay = 200

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(dots) { index ->
            val scale = remember { Animatable(0.5f) }

            LaunchedEffect(Unit) {
                delay((index * animationDelay).toLong())
                while (true) {
                    scale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(durationMillis = 300)
                    )
                    scale.animateTo(
                        targetValue = 0.5f,
                        animationSpec = tween(durationMillis = 300)
                    )
                    delay((dots * animationDelay).toLong())
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(10.dp)
                    .scale(scale.value)
                    .background(
                        color = Color.White,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}