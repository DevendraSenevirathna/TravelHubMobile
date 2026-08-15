package com.travelhub.mobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.travelhub.mobileapp.ui.PlaceholderScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        route = Graph.ROOT
    ) {
        composable(Routes.Splash.route) {
            // Replace with real SplashScreen in the Splash step
            PlaceholderScreen("Splash")
        }

        authGraph(navController)

        composable(Graph.MAIN) {
            MainNavHost()
        }
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Routes.Onboarding.route, route = Graph.AUTH) {
        composable(Routes.Onboarding.route) {
            PlaceholderScreen("Onboarding")
        }
        composable(Routes.Login.route) {
            PlaceholderScreen("Login")
        }
        composable(Routes.Register.route) {
            PlaceholderScreen("Register")
        }
    }
}
