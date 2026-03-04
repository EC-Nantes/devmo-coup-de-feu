package com.vort.devmo_coup_de_feu.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vort.devmo_coup_de_feu.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * ViewModel partagé entre StationDashboard (Sofia) et ChefDashboard (Marc).
 *
 * Architecture :
 * - Source unique de vérité = liste de TableOrder (chaque table a ses stations froid/poisson/viande)
 * - Sofia voit les tickets poisson extraits des commandes où poisson.ready < poisson.total
 * - Quand Sofia valide un ticket, poisson.ready++ dans la TableOrder correspondante
 * - Marc voit les barres de progression se mettre à jour en temps réel
 *
 * Sauvegarde d'état :
 * - Utilise SavedStateHandle pour persister l'état en cas de destruction du process
 */
class CoupDeFeuViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_ORDERS = "orders_state"
        private const val KEY_DATA_VERSION = "data_version"
        private const val DATA_VERSION = 2 // Incrémenter à chaque changement des données initiales

        // Données initiales — toutes les commandes de tables avec les 3 stations
        val INITIAL_ORDERS = listOf(
            TableOrder(1, 12, 4, "8 min",
                Stations(StationProgress(2,2), StationProgress(1,2), StationProgress(0,1)),
                TableStatus.WARNING
            ),
            TableOrder(2, 8, 2, "5 min",
                Stations(StationProgress(2,2), StationProgress(0,2), StationProgress(0,1)),
                TableStatus.ACTIVE
            ),
            TableOrder(3, 21, 6, "12 min",
                Stations(StationProgress(3,3), StationProgress(2,3), StationProgress(1,2)),
                TableStatus.CRITICAL
            ),
            TableOrder(4, 15, 3, "6 min",
                Stations(StationProgress(1,1), StationProgress(0,2), StationProgress(1,2)),
                TableStatus.ACTIVE
            ),
            TableOrder(5, 7, 2, "3 min",
                Stations(StationProgress(1,2), StationProgress(0,1), StationProgress(0,1)),
                TableStatus.ACTIVE
            ),
            TableOrder(6, 19, 5, "15 min",
                Stations(StationProgress(2,2), StationProgress(3,3), StationProgress(2,2)),
                TableStatus.CRITICAL
            ),
            TableOrder(7, 4, 4, "7 min",
                Stations(StationProgress(2,2), StationProgress(1,3), StationProgress(0,1)),
                TableStatus.ACTIVE
            ),
            TableOrder(8, 23, 2, "4 min",
                Stations(StationProgress(1,2), StationProgress(0,1), StationProgress(0,1)),
                TableStatus.ACTIVE
            ),
        )
    }

    // ── Source unique de vérité ────────────────────────────────────
    private val _orders = MutableStateFlow(
        if (savedStateHandle.get<Int>(KEY_DATA_VERSION) == DATA_VERSION) {
            restoreOrders() ?: INITIAL_ORDERS
        } else {
            // Version différente → données périmées, on repart de zéro
            savedStateHandle[KEY_DATA_VERSION] = DATA_VERSION
            INITIAL_ORDERS
        }
    )
    val orders: StateFlow<List<TableOrder>> = _orders

    // ── Tickets de Sofia = dérivés des commandes ──────────────────
    // Sofia voit un ticket pour chaque table qui a du poisson non encore prêt
    val tickets: StateFlow<List<Ticket>> = _orders.map { orderList ->
        orderList
            .filter { it.stations.poisson.ready < it.stations.poisson.total }
            .mapIndexed { index, order ->
                val remaining = order.stations.poisson.total - order.stations.poisson.ready
                val itemNames = generateFishItems(order.table, remaining)
                Ticket(
                    id = order.id,
                    table = order.table,
                    items = itemNames,
                    time = order.time,
                    priority = if (order.status == TableStatus.CRITICAL) Priority.URGENT else Priority.NORMAL
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback overlay
    private val _showFeedback = MutableStateFlow(false)
    val showFeedback: StateFlow<Boolean> = _showFeedback

    private val _feedbackType = MutableStateFlow("check")
    val feedbackType: StateFlow<String> = _feedbackType

    // ── Actions Sofia ─────────────────────────────────────────────

    /**
     * Sofia valide un ticket poisson → poisson.ready++ dans la table correspondante.
     * La barre de progression dans la vue de Marc se met à jour.
     */
    fun validateTicket(ticketId: Int, type: String) {
        _feedbackType.value = type
        _showFeedback.value = true

        viewModelScope.launch {
            delay(1000)
            _showFeedback.value = false
            delay(300)
            // Incrémenter le poisson.ready dans la commande correspondante
            _orders.value = _orders.value.map { order ->
                if (order.id == ticketId && order.stations.poisson.ready < order.stations.poisson.total) {
                    val newPoisson = order.stations.poisson.copy(
                        ready = order.stations.poisson.ready + 1
                    )
                    order.copy(stations = order.stations.copy(poisson = newPoisson))
                } else {
                    order
                }
            }
            saveOrders()
        }
    }

    // ── Actions Marc ──────────────────────────────────────────────

    /**
     * Marc valide une table entière → la table est retirée de la liste.
     */
    fun validateTable(orderId: Int, type: String) {
        _feedbackType.value = type
        _showFeedback.value = true

        viewModelScope.launch {
            delay(1000)
            _showFeedback.value = false
            delay(300)
            _orders.value = _orders.value.filter { it.id != orderId }
            saveOrders()
        }
    }

    // ── Sauvegarde / Restauration d'état ──────────────────────────

    private fun saveOrders() {
        // Sérialise en ArrayList<String> pour SavedStateHandle
        val serialized = ArrayList(_orders.value.map { order ->
            "${order.id}|${order.table}|${order.covers}|${order.time}|" +
            "${order.stations.froid.ready},${order.stations.froid.total}|" +
            "${order.stations.poisson.ready},${order.stations.poisson.total}|" +
            "${order.stations.viande.ready},${order.stations.viande.total}|" +
            order.status.name
        })
        savedStateHandle[KEY_ORDERS] = serialized
    }

    private fun restoreOrders(): List<TableOrder>? {
        val serialized: List<String>? = savedStateHandle[KEY_ORDERS]
        return serialized?.mapNotNull { line ->
            try {
                val parts = line.split("|")
                val froidParts = parts[4].split(",")
                val poissonParts = parts[5].split(",")
                val viandeParts = parts[6].split(",")
                TableOrder(
                    id = parts[0].toInt(),
                    table = parts[1].toInt(),
                    covers = parts[2].toInt(),
                    time = parts[3],
                    stations = Stations(
                        froid = StationProgress(froidParts[0].toInt(), froidParts[1].toInt()),
                        poisson = StationProgress(poissonParts[0].toInt(), poissonParts[1].toInt()),
                        viande = StationProgress(viandeParts[0].toInt(), viandeParts[1].toInt())
                    ),
                    status = TableStatus.valueOf(parts[7])
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    // Génère des noms de plats poisson en fonction de la table
    private fun generateFishItems(table: Int, count: Int): List<String> {
        val allFishDishes = listOf(
            "Saumon mi-cuit", "Filet de bar", "Bar en croûte de sel",
            "Dorade royale", "Sole meunière", "Saint-Jacques poêlées",
            "Homard grillé", "Cabillaud rôti", "Truite aux amandes"
        )
        // Utilise le numéro de table comme seed pour avoir des plats cohérents
        val startIndex = (table * 7) % allFishDishes.size
        return (0 until count).map { i ->
            allFishDishes[(startIndex + i) % allFishDishes.size]
        }
    }
}
