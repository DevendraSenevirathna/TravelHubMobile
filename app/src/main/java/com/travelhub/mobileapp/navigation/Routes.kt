package com.travelhub.mobileapp.navigation

sealed class Routes(val route: String) {
    // Auth flow
    object Splash : Routes("splash")
    object Onboarding : Routes("onboarding")
    object Login : Routes("login")
    object Register : Routes("register")

    // Bottom nav destinations
    object Home : Routes("home")
    object Explore : Routes("explore")
    object Services : Routes("services")
    object Favorites : Routes("favorites")
    object Profile : Routes("profile")

    // Detail / secondary screens
    object DestinationDetails : Routes("destination_details/{spotId}") {
        fun createRoute(spotId: Int) = "destination_details/$spotId"
    }
    object CreatePost : Routes("create_post")
    object CreateSpot : Routes("create_spot")
    object PostDetails : Routes("post_details/{postId}") {
        fun createRoute(postId: Int) = "post_details/$postId"
    }
    object EditPost : Routes("edit_post/{postId}") {
        fun createRoute(postId: Int) = "edit_post/$postId"
    }
    object ServiceDetails : Routes("service_details/{serviceId}") {
        fun createRoute(serviceId: Int) = "service_details/$serviceId"
    }
    object BookingForm : Routes("booking_form/{serviceId}") {
        fun createRoute(serviceId: Int) = "booking_form/$serviceId"
    }
    object BookingConfirmation : Routes("booking_confirmation")
    object BookingHistory : Routes("booking_history")
    object EditProfile : Routes("edit_profile")
    object Settings : Routes("settings")
}

// Top-level graph roots — used to decide which "shell" to show
object Graph {
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
    const val ROOT = "root_graph"
}
