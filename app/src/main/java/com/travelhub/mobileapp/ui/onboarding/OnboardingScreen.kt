package com.travelhub.mobileapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.travelhub.mobileapp.ui.AppViewModelFactory

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = viewModel(factory = AppViewModelFactory(context))
    val selectedInterests by viewModel.selectedInterests.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage(
                    selectedInterests = selectedInterests,
                    onToggleInterest = viewModel::toggleInterest
                )
                1 -> FeaturesPage()
                2 -> GetStartedPage()
            }
        }

        // Page indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

        // Bottom action button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage < 2) {
                TextButton(onClick = {
                    viewModel.completeOnboarding(onFinished)
                }) {
                    Text("Skip")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.completeOnboarding(onFinished)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(if (pagerState.currentPage < 2) "Continue" else "Get Started")
            }
        }
    }
}

@Composable
private fun WelcomePage(
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Welcome to Travel Places 🌿",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Discover breathtaking destinations, hidden gems, and real travel experiences shared by a global community of explorers.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp)
        )

        Text(
            "What are you interested in?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 32.dp, bottom = 12.dp)
        )

        FlowRowInterests(
            options = interestOptions,
            selected = selectedInterests,
            onToggle = onToggleInterest
        )
    }
}

@Composable
private fun FlowRowInterests(
    options: List<InterestOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    // Simple wrap layout using rows of chips (avoids needing an extra FlowRow dependency)
    val rows = options.chunked(2)
    Column {
        rows.forEach { rowItems ->
            Row(modifier = Modifier.padding(bottom = 10.dp)) {
                rowItems.forEach { option ->
                    val isSelected = option.label in selected
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggle(option.label) },
                        label = { Text("${option.emoji} ${option.label}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturesPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Explore. Share. Connect.",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Travel Places helps you discover amazing destinations and share your journeys with others.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        featureItems.forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(feature.emoji, style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(feature.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(feature.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            "Community Guidelines",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "To keep Travel Places a positive and inspiring community, please follow these simple guidelines:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        communityGuidelines.forEach { guideline ->
            Text(
                "•  $guideline",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun GetStartedPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Your journey starts here 🌍",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Join thousands of travelers exploring new destinations every day.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        Text(
            "Create an account to save places, share posts, and personalize your travel experience.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}