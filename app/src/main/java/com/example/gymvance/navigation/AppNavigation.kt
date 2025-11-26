package com.example.gymvance

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymvance.screens.HomeScreen
import com.example.gymvance.screens.auth.LoginScreen
import com.example.gymvance.screens.auth.RegisterScreen
import com.example.gymvance.screens.routines.RutinasScreen
import com.example.gymvance.screens.routines.RutinaScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("routines") {
            RutinasScreen(navController = navController)
        }

        composable("routine/{routineId}") { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            RutinaScreen(navController = navController, routineId = routineId)
        }
    }
}
