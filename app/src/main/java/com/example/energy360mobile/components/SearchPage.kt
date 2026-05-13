package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energy360mobile.R
import com.example.energy360mobile.components.noConnection.SearchPageNoConnection
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.services.ServicesEntity
import com.example.energy360mobile.utils.ConnectivityObserver
import com.example.energy360mobile.utils.NetworkObserver
import com.example.energy360mobile.utils.isNetworkAvailable
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.ServicesViewModel
import com.example.energy360mobile.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SearchPage(){
    val context = LocalContext.current

    val connectivityObserver = remember { ConnectivityObserver(context) }
    val status by connectivityObserver.observe().collectAsState(
        initial = if (isNetworkAvailable(context)) NetworkObserver.Status.Available
        else NetworkObserver.Status.Unavailable
    )
    if(status == NetworkObserver.Status.Available)
        SearchPageWithConnection()
    else
        SearchPageNoConnection()
}
@Composable
fun SearchPageWithConnection(viewModel: ServicesViewModel = viewModel(), reservationsViewModel: ReservationsViewModel = viewModel(), userViewModel: UserViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val allServices by viewModel.services.collectAsState()
    val currentReservations by reservationsViewModel.reservations.collectAsState()
    val userData by userViewModel.userData.collectAsState()

    val filteredResults = remember(searchQuery, allServices) {
        val currentTime = System.currentTimeMillis()
        val futureServices = allServices.filter { it.date > currentTime }
        
        if (searchQuery.isEmpty()) {
            futureServices
        } else {
            futureServices.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val categories = listOf("Yoga", "Crossfit", "Nuoto", "Pilates", "Pesi", "Cardio")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //top-box (big-one)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = stringResource(id = R.string.what_doin),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.explore),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-28).dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                placeholder = { Text(stringResource(id = R.string.find_by)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }

        Text(
            text = stringResource(id = R.string.most_wanted),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(4).forEach { category ->
                SuggestionChip(
                    onClick = { searchQuery = category },
                    label = { Text(category) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.suggested_result),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(filteredResults) { service ->
                val isAlreadyReserved = currentReservations.any { 
                    it.eventName == service.name && it.date == service.date 
                }

                SearchItemCard(service, isAlreadyReserved = isAlreadyReserved, onReserve = {
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
                    scope.launch{
                        reservationsViewModel.insertReservation(reservation)
                    }
                })
            }
        }
    }
}

@Composable
fun SearchItemCard(result: ServicesEntity, isAlreadyReserved: Boolean, onReserve: () -> Unit) {
    var openReserveButton by remember { mutableStateOf(false) }

    val categoryIcon = when (result.category) {
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
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
        ),
        onClick = { openReserveButton = !openReserveButton }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
            ) {
                Text(text = result.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = result.category + " • " + result.place, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Text(text = convertLongToDate(result.date) + " • " + result.duration, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
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
                        text = if (isAlreadyReserved) stringResource(id = R.string.already_reserved) else stringResource(id = R.string.reserve),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

fun convertLongToDate(dateMillis: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        val date = Date(dateMillis)
        sdf.format(date)
    } catch (e: Exception) {
        "---"
    }
}