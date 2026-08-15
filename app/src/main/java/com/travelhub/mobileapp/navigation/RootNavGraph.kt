package com.travelhub.mobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.travelhub.mobileapp.ui.AppViewModelFactory
import com.travelhub.mobileapp.ui.PlaceholderScreen
import com.travelhub.mobileapp.ui.splash.SplashScreen
import com.travelhub.mobileapp.ui.splash.SplashViewModel

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        route = Graph.ROOT
    ) {
        composable(Routes.Splash.route) {
            val context = LocalContext.current
            val viewModel: SplashViewModel = viewModel(
                factory = AppViewModelFactory(context)
            )

            SplashScreen(
                viewModel = viewModel,
                onNavigateToOnboarding = {
                    navController.navigate(Graph.AUTH) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth flow — nested graph, inlined directly here
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

        // Main app shell
        composable(Graph.MAIN) {
            MainNavHost()
        }
    }
}