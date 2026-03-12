package com.vort.devmo_coup_de_feu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vort.devmo_coup_de_feu.ui.screens.ChefDashboard
import com.vort.devmo_coup_de_feu.ui.screens.StationDashboard
import com.vort.devmo_coup_de_feu.viewmodel.CoupDeFeuViewModel

object Routes {
    const val STATION = "station"
    const val CHEF = "chef"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CoupDeFeuViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.STATION
    ) {
        composable(Routes.STATION) {
            StationDashboard(viewModel = viewModel)
        }
        composable(Routes.CHEF) {
            ChefDashboard(viewModel = viewModel)
        }
    }
}
