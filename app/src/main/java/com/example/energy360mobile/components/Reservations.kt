package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energy360mobile.R
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Reservations(viewModel: ReservationsViewModel) {
    val reservations by viewModel.reservations.collectAsState()
    val reservedEvents = reservations.filter { it.isReserved }
    val upcomingReservations = reservedEvents.filter { !it.isPast }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //top rounded box (big-one)
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

        //column with activities/hint message for reservations
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) ,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (reservedEvents.isEmpty()) stringResource(id = R.string.no_reservations) else stringResource(id = R.string.next_reservations),
                style = MaterialTheme.typography.titleLarge,
                color = if (reservedEvents.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (reservedEvents.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.reservation_hint), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                upcomingReservations.forEach { event ->
                    ReservedEventCard(event, onDelete = { viewModel.deleteReservation(it) })
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ReservedEventCard(event: ReservationsEntity, onDelete: (ReservationsEntity) -> Unit) {
    val sdf = SimpleDateFormat("dd MMMM - HH:mm", Locale.getDefault())
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
            //left event container
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Event,
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
                        .background(Color.White, RoundedCornerShape(10.dp)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            //text of the container
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null,
                        modifier = Modifier
                        .size(14.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Text(text = " " + formattedDate + " (" + event.place + ")",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            //delete button
            IconButton(onClick = { onDelete(event) }) {
                Icon(Icons.Default.Cancel,
                    contentDescription = stringResource(id = R.string.delete_reservation),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}
