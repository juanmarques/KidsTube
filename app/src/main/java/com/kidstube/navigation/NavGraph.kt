package com.kidstube.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kidstube.feature.home.HomeScreen
import com.kidstube.feature.onboarding.OnboardingScreen
import com.kidstube.feature.parental.PinEntryScreen
import com.kidstube.feature.parental.SettingsScreen
import com.kidstube.feature.player.PlayerScreen
import com.kidstube.feature.search.SearchScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SEARCH = "search"
    const val PLAYER = "player/{videoId}"
    const val PIN_ENTRY = "pin_entry"
    const val SETTINGS = "settings"

    fun player(videoId: String) = "player/$videoId"
}

@Composable
fun KidsTubeNavGraph() {
    val navController = rememberNavController()
    val startDestViewModel: StartDestinationViewModel = hiltViewModel()
    val startDest by startDestViewModel.startDestination.collectAsState()

    startDest?.let { dest ->
        NavHost(
            navController = navController,
            startDestination = dest
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onVideoClick = { videoId -> navController.navigate(Routes.player(videoId)) },
                    onSettingsClick = { navController.navigate(Routes.PIN_ENTRY) }
                )
            }
            // Search is only accessible from Settings (behind PIN)
            composable(Routes.SEARCH) {
                SearchScreen(
                    onVideoClick = { videoId -> navController.navigate(Routes.player(videoId)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.PLAYER,
                arguments = listOf(navArgument("videoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
                PlayerScreen(
                    videoId = videoId,
                    onBack = { navController.popBackStack() },
                    onVideoClick = { nextId ->
                        navController.navigate(Routes.player(nextId)) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.PIN_ENTRY) {
                PinEntryScreen(
                    onSuccess = {
                        navController.navigate(Routes.SETTINGS) {
                            popUpTo(Routes.PIN_ENTRY) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onSearchClick = {
                        navController.navigate(Routes.SEARCH)
                    }
                )
            }
        }
    }
}
