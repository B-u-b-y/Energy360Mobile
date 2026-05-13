package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energy360mobile.R
import java.util.Calendar

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val day: String
)

val workoutPlan = listOf(
    Exercise("Panca Piana", 4, "10", "Mon"),
    Exercise("Squat", 3, "12", "Mon"),
    Exercise("Trazioni", 4, "8", "Wed"),
    Exercise("Stacco da Terra", 3, "6", "Wed"),
    Exercise("Military Press", 4, "10", "Fri"),
    Exercise("Leg Press", 3, "15", "Fri")
)

@Composable
fun TrainingSession(onBack: () -> Unit) {
    val dayKeys = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayLabels = listOf(
        stringResource(id = R.string.monday_day),
        stringResource(id = R.string.tuesday_day),
        stringResource(id = R.string.wednesday_day),
        stringResource(id = R.string.thursday_day),
        stringResource(id = R.string.friday_day),
        stringResource(id = R.string.saturday_day),
        stringResource(id = R.string.sunday_day),
    )

    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    val currentDayKey = when (dayOfWeek) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        Calendar.SUNDAY -> "Sun"
        else -> "Mon"
    }

    var selectedDay by remember { mutableStateOf(currentDayKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = stringResource(id = R.string.training_intro),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(id = R.string.training_title),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Days Filter
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-30).dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyRow(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dayKeys.size) { index ->
                        val key = dayKeys[index]
                        val label = dayLabels[index]
                        DayChip(
                            day = label,
                            isSelected = selectedDay == key,
                            onClick = { selectedDay = key }
                        )
                    }
                }
            }
        }

        // Exercises List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredExercises = workoutPlan.filter { it.day == selectedDay }

            if (filteredExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.no_training), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                filteredExercises.forEach { exercise ->
                    ExerciseCard(exercise)
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun DayChip(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ExerciseCard(exercise: Exercise) {
    var completed by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val weights = remember { mutableStateListOf<String>().apply {
        if (isEmpty()) repeat(exercise.sets) { add("") } 
    } }
    val repsDone = remember { mutableStateListOf<String>().apply { 
        if (isEmpty()) repeat(exercise.sets) { add("") } 
    } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (completed) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = if (completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Column(modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${exercise.sets} serie x ${exercise.reps} rip.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = { completed = !completed }) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(id = R.string.done_exercise),
                        tint = if (completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                for (i in 0 until exercise.sets) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${i + 1}°",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(20.dp)
                        )
                        OutlinedTextField(
                            value = weights[i],
                            onValueChange = { weights[i] = it },
                            label = { Text("Kg", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = repsDone[i],
                            onValueChange = { repsDone[i] = it },
                            label = { Text("Reps", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
