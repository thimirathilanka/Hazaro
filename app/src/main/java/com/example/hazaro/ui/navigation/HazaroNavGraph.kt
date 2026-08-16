package com.example.hazaro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hazaro.ui.auth.AuthScreen
import com.example.hazaro.ui.auth.AuthViewModel
import com.example.hazaro.ui.home.HomeScreen
import com.example.hazaro.ui.home.HomeViewModel
import com.example.hazaro.ui.report.AddReportScreen
import com.example.hazaro.ui.report.AddReportViewModel

object Routes {
    const val HOME = "home"
    const val AUTH = "auth?returnTo={returnTo}"
    const val ADD_REPORT = "add_report"
    const val RETURN_TO_ADD = "add"
    const val RETURN_TO_HOME = "home"

    fun auth(returnTo: String = RETURN_TO_HOME): String = "auth?returnTo=$returnTo"
}

@Composable
fun HazaroNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onAddReport = {
                    if (viewModel.currentUser() != null) {
                        navController.navigate(Routes.ADD_REPORT)
                    } else {
                        navController.navigate(Routes.auth(Routes.RETURN_TO_ADD))
                    }
                },
                onOpenAccount = {
                    if (viewModel.uiState.value.isSignedIn) {
                        viewModel.showSignOutDialog(true)
                    } else {
                        navController.navigate(Routes.auth(Routes.RETURN_TO_HOME))
                    }
                },
            )
        }
        composable(
            route = Routes.AUTH,
            arguments = listOf(
                navArgument("returnTo") {
                    type = NavType.StringType
                    defaultValue = Routes.RETURN_TO_HOME
                },
            ),
        ) { entry ->
            val returnTo = entry.arguments?.getString("returnTo") ?: Routes.RETURN_TO_HOME
            val viewModel: AuthViewModel = viewModel()
            AuthScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAuthenticated = {
                    if (returnTo == Routes.RETURN_TO_ADD) {
                        navController.navigate(Routes.ADD_REPORT) {
                            popUpTo(Routes.HOME)
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
            )
        }
        composable(Routes.ADD_REPORT) {
            val viewModel: AddReportViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(uiState.requiresAuth) {
                if (uiState.requiresAuth) {
                    navController.navigate(Routes.auth(Routes.RETURN_TO_ADD)) {
                        popUpTo(Routes.ADD_REPORT) { inclusive = true }
                    }
                }
            }
            LaunchedEffect(uiState.saved) {
                if (uiState.saved) {
                    navController.popBackStack()
                }
            }
            AddReportScreen(
                viewModel = viewModel,
                onClose = { navController.popBackStack() },
            )
        }
    }
}
