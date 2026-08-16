package com.travelhub.mobileapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.travelhub.mobileapp.ui.PlaceholderScreen
import com.travelhub.mobileapp.ui.home.HomeScreen
import com.travelhub.mobileapp.ui.explore.ExploreScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.travelhub.mobileapp.ui.destinations.DestinationDetailsScreen
import com.travelhub.mobileapp.ui.favorites.FavoritesScreen
import com.travelhub.mobileapp.ui.posts.CreatePostScreen
import com.travelhub.mobileapp.ui.posts.PostDetailsScreen
import com.travelhub.mobileapp.ui.services.ServicesScreen
import com.travelhub.mobileapp.ui.services.ServiceDetailsScreen
import com.travelhub.mobileapp.ui.services.BookingFormScreen
import com.travelhub.mobileapp.ui.services.BookingConfirmationScreen
import com.travelhub.mobileapp.ui.services.BookingHistoryScreen

@Composable
fun MainNavHost() {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    onSpotClick = { spotId ->
                        bottomNavController.navigate(Routes.DestinationDetails.createRoute(spotId))
                    },
                    onPostClick = { postId ->
                        bottomNavController.navigate(Routes.PostDetails.createRoute(postId))
                    },
                    onSearchClick = {
                        bottomNavController.navigate(Routes.Explore.route)
                    }
                )
            }
            composable(Routes.Explore.route) {
                ExploreScreen(
                    onSpotClick = { spotId ->
                        bottomNavController.navigate(Routes.DestinationDetails.createRoute(spotId))
                    }
                )
            }
            composable(Routes.Services.route) {
                ServicesScreen(
                    onServiceClick = { serviceId ->
                        bottomNavController.navigate(Routes.ServiceDetails.createRoute(serviceId))
                    },
                    onBookingHistoryClick = {
                        bottomNavController.navigate(Routes.BookingHistory.route)
                    }
                )
            }

            composable(
                route = Routes.ServiceDetails.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: return@composable
                ServiceDetailsScreen(
                    serviceId = serviceId,
                    onBackClick = { bottomNavController.popBackStack() },
                    onBookClick = { id -> bottomNavController.navigate(Routes.BookingForm.createRoute(id)) }
                )
            }

            composable(
                route = Routes.BookingForm.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: return@composable
                BookingFormScreen(
                    serviceId = serviceId,
                    onBackClick = { bottomNavController.popBackStack() },
                    onBookingConfirmed = {
                        bottomNavController.navigate(Routes.BookingConfirmation.route) {
                            popUpTo(Routes.Services.route)
                        }
                    }
                )
            }

            composable(Routes.BookingConfirmation.route) {
                BookingConfirmationScreen(
                    onDoneClick = {
                        bottomNavController.navigate(Routes.Services.route) {
                            popUpTo(Routes.Services.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Favorites.route) {
                FavoritesScreen(
                    onSpotClick = { spotId ->
                        bottomNavController.navigate(Routes.DestinationDetails.createRoute(spotId))
                    }
                )
            }
            composable(Routes.Profile.route) { PlaceholderScreen("Profile") }

            // Detail screens reachable from within the main shell
            composable(Routes.CreatePost.route) {
                CreatePostScreen(
                    onBackClick = { bottomNavController.popBackStack() },
                    onSubmitSuccess = { bottomNavController.popBackStack() }
                )
            }

            composable(
                route = Routes.PostDetails.route,
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
                PostDetailsScreen(
                    postId = postId,
                    onBackClick = { bottomNavController.popBackStack() },
                    onEditClick = { id -> bottomNavController.navigate(Routes.EditPost.createRoute(id)) },
                    onDeleted = { bottomNavController.popBackStack() }
                )
            }

            composable(
                route = Routes.EditPost.route,
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
                CreatePostScreen(
                    editingPostId = postId,
                    onBackClick = { bottomNavController.popBackStack() },
                    onSubmitSuccess = { bottomNavController.popBackStack() }
                )
            }
            composable(Routes.EditProfile.route) { PlaceholderScreen("Edit Profile") }
            composable(Routes.Settings.route) { PlaceholderScreen("Settings") }
            composable(Routes.BookingHistory.route) {
                BookingHistoryScreen()
            }

            composable(
                route = Routes.DestinationDetails.route,
                arguments = listOf(navArgument("spotId") { type = NavType.IntType })
            ) { backStackEntry ->
                val spotId = backStackEntry.arguments?.getInt("spotId") ?: return@composable
                DestinationDetailsScreen(
                    spotId = spotId,
                    onBackClick = { bottomNavController.popBackStack() },
                    onPostClick = { postId ->
                        bottomNavController.navigate(Routes.PostDetails.createRoute(postId))
                    }
                )
            }
        }
    }
}
