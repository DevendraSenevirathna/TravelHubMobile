package com.travelhub.mobileapp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelhub.mobileapp.R

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel
) {
    val destination by viewModel.destination.collectAsState()

    // Logo scale animation state (starts small at 0.3f)
    val logoScale = remember { Animatable(0.3f) }

    // Start logo animation immediately on screen load
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Navigation trigger
    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.ONBOARDING -> onNavigateToOnboarding()
            SplashDestination.AUTH -> onNavigateToAuth()
            SplashDestination.MAIN -> onNavigateToMain()
            SplashDestination.LOADING -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_splash_mountains),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Animated Center Logo (Pops in smoothly)
        Image(
            painter = painterResource(id = R.drawable.ic_travelhub_logo),
            contentDescription = "TravelHub Logo",
            modifier = Modifier
                .size(140.dp)
                .scale(logoScale.value)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )

        // 3. Bottom Inline Loader & "Loading..." Text (Visible immediately)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color(0xFF90EE90),
                strokeWidth = 2.5.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Loading...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}