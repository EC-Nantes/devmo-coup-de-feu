package com.vort.devmo_coup_de_feu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vort.devmo_coup_de_feu.ui.screens.DashboardScaffold
import com.vort.devmo_coup_de_feu.ui.theme.DevmocoupdefeuTheme
import com.vort.devmo_coup_de_feu.viewmodel.CoupDeFeuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevmocoupdefeuTheme {
                val viewModel: CoupDeFeuViewModel = viewModel()
                DashboardScaffold(viewModel = viewModel)
            }
        }
    }
}