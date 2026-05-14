package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energy360mobile.R
import com.example.energy360mobile.components.noConnection.ReservationsNoConnection
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.utils.ConnectivityObserver
import com.example.energy360mobile.utils.NetworkObserver
import com.example.energy360mobile.utils.isNetworkAvailable
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalLocale

/**
 * The entry point for the Reservations screen.
 * It manages connectivity status and chooses whether to show the reservations list
 * or a "No Connection" screen.
 *
 * @param viewModel The ReservationsViewModel managing the user's bookings.
 */
@Composable
fun Reservations(viewModel: ReservationsViewModel){
    val context = LocalContext.current

    // Observe network connectivity to handle offline state
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val status by connectivityObserver.observe().collectAsState(
        initial = if (isNetworkAvailable(context)) NetworkObserver.Status.Available
        else NetworkObserver.Status.Unavailable
    )

    if(status == NetworkObserver.Status.Available)
        ReservationsWithConnection(viewModel)
    else
        ReservationsNoConnection()
}

/**
 * Displays the list of upcoming reservations when a network connection is available.
 * Shows a header and a list of booked events or a hint if no reservations exist.
 *
 * @param viewModel The ViewModel providing the reservation data.
 */
@Composable
fun ReservationsWithConnection(viewModel: ReservationsViewModel) {
    val reservations by viewModel.reservations.collectAsState()
    val reservedEvents = reservations.filter { it.isReserved }
    val upcomingReservations = reservedEvents.filter { !it.isPast }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header section with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = stringResource(id = R.string.recap_reservation),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.reservation_title),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                )
            }
        }

        // Main content: list of reservations or empty state message
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (reservedEvents.isEmpty()) stringResource(id = R.string.no_reservations) else stringResource(
                    id = R.string.next_reservations
                ),
                style = MaterialTheme.typography.titleLarge,
                color = if (reservedEvents.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (reservedEvents.isEmpty()) {
                // Hint card shown when there are no reservations
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(id = R.string.reservation_hint),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // List of upcoming booked events
                upcomingReservations.forEach { event ->
                    ReservedEventCard(event, onDelete = { viewModel.deleteReservation(it) })
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * A card representing a single reserved event.
 * Displays event name, date, time, and location, with an option to cancel.
 *
 * @param event The reservation entity to display.
 * @param onDelete Callback triggered when the delete/cancel button is clicked.
 */
@Composable
fun ReservedEventCard(event: ReservationsEntity, onDelete: (ReservationsEntity) -> Unit) {
    val sdf = SimpleDateFormat("dd MMMM - HH:mm", LocalLocale.current.platformLocale)
    val formattedDate = sdf.format(Date(event.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon section with a checkmark badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(10.dp)
                        ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Event details: Name, date, and place
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule, contentDescription = null,
                        modifier = Modifier
                            .size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " " + formattedDate + " (" + event.place + ")",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Cancel/Delete reservation button
            IconButton(onClick = { onDelete(event) }) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = stringResource(id = R.string.delete_reservation),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}