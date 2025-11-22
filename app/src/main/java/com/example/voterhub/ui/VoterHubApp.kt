package com.example.voterhub.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voterhub.ui.screens.HomeScreen
import com.example.voterhub.ui.screens.LanguageSelectionScreen
import com.example.voterhub.ui.screens.VoterListScreen
import com.example.voterhub.ui.screens.WelcomeScreen
import com.example.voterhub.ui.theme.VoterHubTheme
import com.example.voterhub.ui.viewmodel.HomeViewModel
import com.example.voterhub.ui.viewmodel.VoterListViewModel

@Composable
fun VoterHubApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
        }
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLanguage = {
                    navController.navigate("language_selection") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("language_selection") {
            LanguageSelectionScreen(
                onLanguageSelected = { language ->
                    // You might want to save the language preference here
                    navController.navigate("home") {
                        popUpTo("language_selection") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                state = state,
                onSectionSelected = viewModel::onSectionSelected,
                onVillageSelected = { sectionId, prabhagId ->
                    navController.navigate("voter_list/$sectionId?prabhagId=$prabhagId")
                },
                onPullToRefresh = viewModel::refreshSections
            )
        }

        composable(
            route = "voter_list/{sectionId}?prabhagId={prabhagId}",
            arguments = listOf(
                androidx.navigation.navArgument("sectionId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("prabhagId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                }
            )
        ) {
            val viewModel: VoterListViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            VoterListScreen(
                state = state,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onRecentSearchSelected = viewModel::onRecentSearchSelected,
                onAgeRangeSelected = viewModel::onAgeRangeSelected,
                onCustomAgeRangeSelected = viewModel::onCustomAgeRangeSelected,
                onClearCustomAgeRangeError = viewModel::clearCustomAgeRangeError,
                onGenderFilterSelected = viewModel::onGenderFilterSelected,
                onClearFilters = viewModel::clearFilters,
                onToggleFilterSheet = viewModel::toggleFilterSheet,
                onLoadNextPage = viewModel::loadNextPage,
                onPullToRefresh = viewModel::onPullToRefresh,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
