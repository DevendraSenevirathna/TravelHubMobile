package com.travelhub.mobileapp.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.R
import com.travelhub.mobileapp.ui.AppViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = viewModel(factory = AppViewModelFactory(context))
    val selectedInterests by viewModel.selectedInterests.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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

        // Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (selected) 22.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) Color(0xFF4CAF50)
                            else Color(0xFFE0E0E0)
                        )
                )
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage < 2) {
                TextButton(
                    onClick = { viewModel.completeOnboarding(onFinished) }
                ) {
                    Text(
                        text = "Skip",
                        color = Color(0xFF4CAF50),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .height(48.dp)
                    .defaultMinSize(minWidth = if (pagerState.currentPage == 2) 160.dp else 135.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < 2) "Continue" else "Get Started 🚀",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
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
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_onboarding_1),
            contentDescription = "Travel Collage",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(horizontal = 22.dp)
                .padding(top = 20.dp, bottom = 14.dp)
        ) {
            Text(
                text = "Welcome to Travel Places 🌿",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Text(
                text = "Discover breathtaking destinations, hidden gems, and real travel experiences shared by a global community.",
                fontSize = 12.5.sp,
                color = Color(0xFF666666),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "What are you interested in?",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
            )

            FlowRowInterests(
                options = interestOptions,
                selected = selectedInterests,
                onToggle = onToggleInterest
            )
        }
    }
}

@Composable
private fun FlowRowInterests(
    options: List<InterestOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    val rows = options.chunked(2)
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    val isSelected = option.label in selected
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggle(option.label) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE5E5E5)
                        ),
                        shadowElevation = if (isSelected) 0.dp else 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = option.emoji, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF424242)
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
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
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Explore. Share. Connect.",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Travel Places helps you discover amazing destinations and share your journeys with others.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF616161),
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            featureItems.forEach { feature ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFEEEEEE)
                    ),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(feature.emoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feature.title,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = feature.description,
                                fontSize = 13.sp,
                                color = Color(0xFF757575),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun GetStartedPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // 1. Globe Hero Icon Badge
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌍", fontSize = 48.sp)
        }

        // 2. Titles Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Your journey starts here",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Join thousands of travelers exploring new destinations, creating stories, and finding unique stays every day.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF616161),
                lineHeight = 20.sp
            )
        }

        // 3. Highlight Perks Card (Fills screen pleasantly)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF9FBF9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8F5E9)),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HighlightRow(icon = "✨", title = "Save Places", subtitle = "Create custom wishlists for your dream trips")
                HighlightRow(icon = "📸", title = "Share Moments", subtitle = "Post reviews, photos, and guide others")
                HighlightRow(icon = "🛡️", title = "Travel Confidently", subtitle = "Verified community suggestions and tips")
            }
        }

        Text(
            text = "Ready to start your next adventure?",
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HighlightRow(icon: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
        }
    }
}