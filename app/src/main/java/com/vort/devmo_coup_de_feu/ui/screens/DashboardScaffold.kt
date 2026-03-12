package com.vort.devmo_coup_de_feu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vort.devmo_coup_de_feu.navigation.Routes
import com.vort.devmo_coup_de_feu.ui.theme.*
import com.vort.devmo_coup_de_feu.viewmodel.CoupDeFeuViewModel

@Composable
fun DashboardScaffold(viewModel: CoupDeFeuViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isChefView = currentRoute == Routes.CHEF

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ── Header ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Black)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔥",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "COUP DE FEU",
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = (-0.5).sp
                )
            }

            Button(
                onClick = {
                    if (isChefView) {
                        navController.navigate(Routes.STATION) {
                            popUpTo(Routes.STATION) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.CHEF) {
                            popUpTo(Routes.STATION) { inclusive = false }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = if (isChefView) "🔥 Vue Poste" else "👨‍🍳 Vue Chef",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bordure fine sous le header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceBorder)
        )

        // ── Contenu ───────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
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
    }
}
