package com.shaalevikas.app.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.shaalevikas.app.ui.screens.*
import com.shaalevikas.app.viewmodel.AppViewModel

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: AppViewModel) {
    val role by viewModel.sessionRole.collectAsState()
    val allNeeds by viewModel.allNeeds.collectAsState()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // Splash
        composable(Routes.SPLASH) {
            SplashScreen(
                role = role,
                onReady = { currentRole ->
                    val dest = when (currentRole) {
                        "alumni" -> Routes.ALUMNI_HOME
                        "admin"  -> Routes.ADMIN_HOME
                        else     -> Routes.ROLE_SELECT
                    }
                    navController.navigate(dest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Role select
        composable(Routes.ROLE_SELECT) {
            RoleSelectScreen(
                onAlumni = { navController.navigate(Routes.ALUMNI_LOGIN) },
                onAdmin  = { navController.navigate(Routes.ADMIN_LOGIN) }
            )
        }

        // Alumni login
        composable(Routes.ALUMNI_LOGIN) {
            AlumniLoginScreen(
                viewModel  = viewModel,
                onSuccess  = {
                    navController.navigate(Routes.ALUMNI_HOME) {
                        popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Routes.ALUMNI_REG) },
                onBack     = { navController.popBackStack() }
            )
        }

        // Alumni register
        composable(Routes.ALUMNI_REG) {
            AlumniRegisterScreen(
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Routes.ALUMNI_HOME) {
                        popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Admin login
        composable(Routes.ADMIN_LOGIN) {
            AdminLoginScreen(
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Alumni home (3 tabs)
        composable(Routes.ALUMNI_HOME) {
            AlumniHomeScreen(
                viewModel    = viewModel,
                onNeedDetail = { needId -> navController.navigate(Routes.needDetail(needId)) },
                onLogout     = {
                    viewModel.logout()
                    navController.navigate(Routes.ROLE_SELECT) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Need detail
        composable(Routes.NEED_DETAIL) { backStack ->
            val needId = backStack.arguments?.getString("needId") ?: return@composable
            NeedDetailScreen(
                needId    = needId,
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        // Admin home
        composable(Routes.ADMIN_HOME) {
            AdminHomeScreen(
                viewModel  = viewModel,
                onAddNeed  = { navController.navigate(Routes.ADD_NEED) },
                onEditNeed = { needId -> navController.navigate(Routes.editNeed(needId)) },
                onLogout   = {
                    viewModel.logout()
                    navController.navigate(Routes.ROLE_SELECT) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Add need
        composable(Routes.ADD_NEED) {
            AddEditNeedScreen(
                viewModel    = viewModel,
                existingNeed = null,
                onDone       = { navController.popBackStack() }
            )
        }

        // Edit need
        composable(Routes.EDIT_NEED) { backStack ->
            val needId = backStack.arguments?.getString("needId") ?: return@composable
            val need   = allNeeds.find { it.id == needId }
            AddEditNeedScreen(
                viewModel    = viewModel,
                existingNeed = need,
                onDone       = { navController.popBackStack() }
            )
        }
    }
}
