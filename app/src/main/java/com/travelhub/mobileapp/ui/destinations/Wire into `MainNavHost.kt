composable(
    route = Routes.DestinationDetails.route,
    arguments = listOf(navArgument("spotId") { type = NavType.IntType })
) { backStackEntry ->
    val spotId = backStackEntry.arguments?.getInt("spotId") ?: return@composable
    DestinationDetailsScreen(
        spotId = spotId,
        onBackClick = { bottomNavController.popBackStack() }
    )
}
