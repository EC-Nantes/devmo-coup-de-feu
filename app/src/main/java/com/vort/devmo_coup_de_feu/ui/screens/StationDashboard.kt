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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vort.devmo_coup_de_feu.model.Priority
import com.vort.devmo_coup_de_feu.ui.theme.*
import com.vort.devmo_coup_de_feu.viewmodel.CoupDeFeuViewModel

@Composable
fun StationDashboard(viewModel: CoupDeFeuViewModel) {
    val tickets by viewModel.tickets.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val feedbackType by viewModel.feedbackType.collectAsState()

    val activeTickets = tickets.size
    val urgentTickets = tickets.count { it.priority == Priority.URGENT }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {

            // ── Sidebar gauche ────────────────────────────────────
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .background(Black)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Titre
                    Text("Poste de Sofia", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Station Poisson", color = TextSecondary, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Carte "En cours"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("En cours", color = TextSecondary, fontSize = 14.sp)
                                Text("⏱", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeTickets.toString().padStart(2, '0'),
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 40.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Carte "Alertes"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface)
                            .border(1.dp, Red, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Alertes", color = TextSecondary, fontSize = 14.sp)
                                Text("⚠️", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = urgentTickets.toString().padStart(2, '0'),
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 40.sp
                            )
                        }
                    }
                }

                // Légende
                Column {
                    Text("Légende", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(2.dp, Red, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Urgent", color = TextSecondary, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(2.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Normal", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            // Bordure verticale
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(SurfaceBorder)
            )

            // ── Grille de tickets ─────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    val borderColor = if (ticket.priority == Priority.URGENT) Red else SurfaceBorder
                    val bgColor = if (ticket.priority == Priority.URGENT)
                        Red.copy(alpha = 0.1f) else Surface

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(4.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(24.dp)
                    ) {
                        // Numéro de table
                        Text("Table", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            text = "${ticket.table}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 56.sp,
                            lineHeight = 56.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Items
                        ticket.items.forEach { item ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("•", color = Orange, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item, color = TextPrimary, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Footer : temps + boutons
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(SurfaceBorder)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⏱", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(ticket.time, color = TextSecondary, fontSize = 14.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Bouton geste (validation à distance)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceBorder)
                                        .clickable { viewModel.validateTicket(ticket.id, "hand") }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✋", fontSize = 18.sp)
                                }
                                // Bouton validation tactile
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Orange)
                                        .clickable { viewModel.validateTicket(ticket.id, "check") }
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
