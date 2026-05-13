package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energy360mobile.R
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.services.ServicesEntity
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.ServicesViewModel
import com.example.energy360mobile.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServicesPage(viewModel: ServicesViewModel = viewModel(), reservationsViewModel: ReservationsViewModel = viewModel(), userViewModel: UserViewModel = viewModel(), onBack: () -> Unit) {
    val services by viewModel.services.collectAsState()
    val currentReservations by reservationsViewModel.reservations.collectAsState()
    val userData by userViewModel.userData.collectAsState()
    val scope = rememberCoroutineScope()

    val currentTime = System.currentTimeMillis()
    val futureServices = services.filter { it.date > currentTime }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top rounded box
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
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.courses),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (futureServices.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.no_courses_available),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                futureServices.forEach { service ->
                    val isAlreadyReserved = currentReservations.any {
                        it.eventName == service.name && it.date == service.date
                    }

                    ServiceCard(
                        service = service,
                        isAlreadyReserved = isAlreadyReserved,
                        onReserve = {
                            val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
                            val dayString = sdfDay.format(Date(service.date))

                            val reservation = ReservationsEntity(
                                eventName = service.name,
                                day = dayString,
                                date = service.date,
                                place = service.place,
                                isReserved = true,
                                userId = userData?.id,
                                id = ""
                            )
                            scope.launch {
                                reservationsViewModel.insertReservation(reservation)
                            }
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ServiceCard(service: ServicesEntity, isAlreadyReserved: Boolean, onReserve: () -> Unit) {
    var openReserveButton by remember { mutableStateOf(false) }

    val categoryIcon = when (service.category) {
        "Benessere" -> Icons.Default.SelfImprovement
        "Funzionale", "Forza", "Cardio" -> Icons.Default.FitnessCenter
        "Combattimento" -> Icons.Default.SportsMartialArts
        "Recupero" -> Icons.Default.AccessibilityNew
        else -> Icons.Default.FitnessCenter
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f)
        ),
        onClick = { if (!isAlreadyReserved) openReserveButton = !openReserveButton }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = service.category,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.duration,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• " + service.place,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (openReserveButton || isAlreadyReserved) {
                    Button(
                        onClick = {
                            if (!isAlreadyReserved) {
                                onReserve()
                                openReserveButton = false
                            }
                        },
                        enabled = !isAlreadyReserved,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp),
                        colors = if (isAlreadyReserved)
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.outline
                            ) else ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            text = if (isAlreadyReserved) stringResource(id = R.string.already_reserved) else stringResource(
                                id = R.string.reserve
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (openReserveButton || isAlreadyReserved) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = convertLongToDate(service.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
