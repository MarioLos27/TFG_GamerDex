package com.mariolos27.gamerdex.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mariolos27.gamerdex.presentation.screens.auth.LoginScreen
import com.mariolos27.gamerdex.presentation.screens.auth.RegisterScreen
import com.mariolos27.gamerdex.presentation.screens.dashboard.DashboardScreen
import com.mariolos27.gamerdex.presentation.screens.game.GameDetailRoute
import com.mariolos27.gamerdex.presentation.screens.library.LibraryScreen
import com.mariolos27.gamerdex.presentation.screens.profile.ProfileScreen
import com.mariolos27.gamerdex.presentation.screens.profile.UsernameScreen
import com.mariolos27.gamerdex.presentation.screens.search.SearchScreen
import com.mariolos27.gamerdex.presentation.screens.auth.model.AuthUiState
import com.mariolos27.gamerdex.presentation.screens.auth.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController
) {
    // Obtener el ViewModel de autenticación
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.authUiState.collectAsState().value

    // Determinar la pantalla de inicio basada en el estado de autenticación
    val startDestination = when (authState) {
        is AuthUiState.Success -> Screen.Dashboard.route
        is AuthUiState.Unauthenticated -> Screen.Login.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(tween(400)) { it / 8 } },
        exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(tween(400)) { -it / 8 } },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(tween(400)) { -it / 8 } },
        popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(tween(400)) { it / 8 } }
    ) {
        // PANTALLAS DE AUTENTICACIÓN

        // Pantalla de Login
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Pantalla de Registro
        composable(route = Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Username.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Configuración de Username
        composable(route = Screen.Username.route) {
            UsernameScreen(
                onUsernameCreated = { username ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // PANTALLAS DE LA APLICACIÓN

        // Pantalla de Dashboard
        composable(route = Screen.Dashboard.route) {
            val isAuthenticated = authViewModel.authUiState.collectAsState().value is AuthUiState.Success
            DashboardScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.Details.createRoute(gameId))
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    if (authState is AuthUiState.Success) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                isUserAuthenticated = isAuthenticated
            )
        }

        // Pantalla de Library (contiene búsqueda integrada)
        composable(route = Screen.Library.route) {
            val isAuthenticated = authViewModel.authUiState.collectAsState().value is AuthUiState.Success
            LibraryScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.Details.createRoute(gameId))
                },
                onAddGameClick = {
                    navController.navigate(Screen.Library.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false; saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToReviews = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false; saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    if (authState is AuthUiState.Success) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                isUserAuthenticated = isAuthenticated
            )
        }

        // Pantalla de Search (Búsqueda Global en IGDB)
        composable(route = Screen.Search.route) {
            val isAuthenticated = authViewModel.authUiState.collectAsState().value is AuthUiState.Success
            SearchScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.Details.createRoute(gameId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false; saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    if (authState is AuthUiState.Success) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                isUserAuthenticated = isAuthenticated
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false; saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Detalles
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: 0L
            GameDetailRoute(
                gameId = gameId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false; saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    if (authState is AuthUiState.Success) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }
    }
}
