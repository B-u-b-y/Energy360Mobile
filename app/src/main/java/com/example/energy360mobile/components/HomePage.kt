package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energy360mobile.R
import com.example.energy360mobile.viewmodels.StepsViewModel


@Composable
fun HomePage(rootNavController: NavHostController, stepsViewModel: StepsViewModel = viewModel()) {
    val internalNavController = rememberNavController()

    NavHost(
        navController = internalNavController,
        startDestination = "dashboard/home"
    ) {
        composable("dashboard/home") { HomeDashboard(internalNavController, stepsViewModel) }
        composable(Screen.Search.route) { SearchPage() }
        composable("dashboard/services") { ServicesPage(onBack = { internalNavController.popBackStack() }) }
    }
}

@Composable
fun HomeDashboard(navController: NavHostController, stepsViewModel: StepsViewModel) {
    val stepsEntity by stepsViewModel.steps.collectAsState()
    val currentSteps = stepsEntity?.steps?.toInt() ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                    text = stringResource(id = R.string.welcome),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.app_label),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                )
                Text(
                    text =  stringResource(id = R.string.motto),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Step Counter Card
        StepCounterCard(steps = currentSteps)

        Text(
            text =  stringResource(id = R.string.our_suggestions),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PromotionCard(
                modifier = Modifier.weight(1f),
                title = "Yoga",
                description = stringResource(id = R.string.promo_yoga_desc),
                icon = Icons.Default.SelfImprovement,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            PromotionCard(
                modifier = Modifier.weight(1f),
                title = "Open Day",
                description = stringResource(id = R.string.promo_openday_desc),
                icon = Icons.Default.Celebration,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }

        Text(
            text =  stringResource(id = R.string.fast_actions),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(Modifier.weight(1f),  stringResource(id = R.string.reserve), Icons.Default.CalendarMonth, MaterialTheme.colorScheme.primary, onClick = { navController.navigate(Screen.Search.route)})
                QuickActionCard(Modifier.weight(1f),  stringResource(id = R.string.courses), Icons.Default.FitnessCenter, MaterialTheme.colorScheme.secondary, onClick = { navController.navigate("dashboard/services") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(Modifier.weight(1f),  stringResource(id = R.string.start_membership), Icons.Default.CardMembership, MaterialTheme.colorScheme.tertiary)
                QuickActionCard(Modifier.weight(1f),  stringResource(id = R.string.contact_us), Icons.Default.Email, MaterialTheme.colorScheme.outline)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StepCounterCard(steps: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-20).dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Passi oggi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "$steps",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Minimalist goal indicator
            val progress = (steps.toFloat() / 10000f).coerceIn(0f, 1f)
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
fun PromotionCard(modifier: Modifier, title: String, description: String, icon: ImageVector, containerColor: Color ){
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier, title: String, icon: ImageVector, color: Color, onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick ?: {},
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
