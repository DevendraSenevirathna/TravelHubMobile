package com.travelhub.mobileapp.ui.onboarding

data class InterestOption(val label: String, val emoji: String)

val interestOptions = listOf(
    InterestOption("Beaches", "🏖️"),
    InterestOption("Mountains", "⛰️"),
    InterestOption("Waterfalls", "💦"),
    InterestOption("Hiking Trails", "🥾"),
    InterestOption("Hotels & Stays", "🏨"),
    InterestOption("Adventure Trips", "🧗")
)

data class FeatureItem(val emoji: String, val title: String, val description: String)

val featureItems = listOf(
    FeatureItem("📍", "Discover nearby travel spots", "Find beautiful places around you instantly"),
    FeatureItem("⭐", "Save favorite destinations", "Build your personal travel collection"),
    FeatureItem("📸", "Share travel experiences", "Upload photos, stories, and memories"),
    FeatureItem("🗺️", "Explore community recommendations", "See what other travelers love")
)

val communityGuidelines = listOf(
    "Be respectful to other travelers",
    "Share genuine experiences only",
    "Avoid spam or unrelated content",
    "Help others discover meaningful places",
    "Keep content travel-focused"
)