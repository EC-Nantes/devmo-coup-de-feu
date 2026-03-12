package com.vort.devmo_coup_de_feu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vort.devmo_coup_de_feu.model.TableStatus
import com.vort.devmo_coup_de_feu.ui.theme.*
import com.vort.devmo_coup_de_feu.viewmodel.CoupDeFeuViewModel

@Composable
fun ChefDashboard(viewModel: CoupDeFeuViewModel) {
    val orders by viewModel.orders.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val feedbackType by viewModel.feedbackType.collectAsState()

    val totalTables = orders.size
    val criticalTables = orders.count { it.status == TableStatus.CRITICAL }
    val averageTime = if (orders.isNotEmpty()) {
        orders.map { it.time.replace(" min", "").trim().toIntOrNull() ?: 0 }.average().toInt()
    } else 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            // ── Header avec statistiques ──────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Vue d'ensemble — Chef Marc",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tables actives
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Tables actives",
                        value = "$totalTables",
                        icon = "👥",
                        borderColor = SurfaceBorder
                    )
                    // Critiques
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Critiques",
                        value = "$criticalTables",
                        icon = "📈",
                        borderColor = Red
                    )
                    // Temps moyen
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Temps moyen",
                        value = "${averageTime}m",
                        icon = "⏱",
                        borderColor = SurfaceBorder
                    )
                }
            }

            // Bordure
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SurfaceBorder)
            )

            // ── Grille de commandes ───────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    val (borderColor, bgColor) = when (order.status) {
                        TableStatus.CRITICAL -> Red to Red.copy(alpha = 0.05f)
                        TableStatus.WARNING -> Orange to Orange.copy(alpha = 0.05f)
                        TableStatus.ACTIVE -> SurfaceBorder to Surface
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(20.dp)
                    ) {
                        // En-tête : table + infos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("Table", color = TextSecondary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${order.table}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 42.sp,
                                    lineHeight = 42.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👥", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${order.covers} cvts", color = TextSecondary, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⏱", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(order.time, color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Barres de progression par station
                        val stations = listOf(
                            "Froid" to order.stations.froid,
                            "Poisson" to order.stations.poisson,
                            "Viande" to order.stations.viande
                        )

                        stations.forEach { (name, progress) ->
                            val pct = if (progress.total > 0) {
                                (progress.ready.toFloat() / progress.total * 100)
                            } else 0f
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(name, color = TextPrimary, fontSize = 13.sp)
                                    Text(
                                        "${progress.ready}/${progress.total}",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                // Barre de progression
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceBorder)
                                ) {
                                    if (pct > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction = pct / 100f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(getProgressColor(pct))
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Boutons de validation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Bouton geste (validation à distance)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceBorder)
                                    .clickable { viewModel.validateTable(order.id, "hand") }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✋", fontSize = 18.sp)
                            }
                            // Bouton validation tactile
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Orange)
                                    .clickable { viewModel.validateTable(order.id, "check") }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ── Overlay Feedback ──────────────────────────────────────
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Blue.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (feedbackType == "hand") "✋" else "✓",
                        fontSize = 120.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (feedbackType == "hand") "GESTE DÉTECTÉ" else "VALIDÉ",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

// ── Composant StatCard ────────────────────────────────────────────
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: String,
    borderColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = TextSecondary, fontSize = 14.sp)
                Text(icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )
        }
    }
}

// Couleur dynamique pour les barres de progression
private fun getProgressColor(value: Float): Color {
    return when {
        value >= 80f -> Red
        value >= 50f -> Orange
        else -> Blue
    }
}
