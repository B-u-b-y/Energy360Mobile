package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energy360mobile.R
import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.example.energy360mobile.viewmodels.ExercisesViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class) //Codice trovato per creare il banner dal basso
@Composable
fun TrainingSession(viewModel: ExercisesViewModel = viewModel(), onBack: () -> Unit ) {
    val exercisesFromDb by viewModel.exercise.collectAsState()
    val completedExercises by viewModel.completedExercises.collectAsState()

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
    var showAddSheet by remember { mutableStateOf(false) }
    val availableExercises by viewModel.availableExercises.collectAsState()
    val sheetState = rememberModalBottomSheetState() //Banner basso

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.fetchAvailableExercises()
                    showAddSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_exercise))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
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
                val filteredExercises = exercisesFromDb.filter { it.day == selectedDay }

                if (filteredExercises.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.no_training), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    filteredExercises.forEach { exercise ->
                        ExerciseCard(exercise, onComplete = { completed, weights, reps ->
                            viewModel.updateExerciseCompletion(exercise.id, completed, weights, reps)
                        })
                    }
                }

                if (completedExercises.isNotEmpty()) {
                    val history = completedExercises.filter { it.day != selectedDay || it.isCompleted }
                    if (history.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.completed_exercises_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        history.forEach { exercise ->
                            ExerciseCard(exercise, onComplete = { completed, weights, reps ->
                                viewModel.updateExerciseCompletion(exercise.id, completed, weights, reps)
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddExerciseContent(
                availableExercises = availableExercises,
                onAdd = { name, sets, reps ->
                    viewModel.insertExercise(
                        ExercisesEntity(
                            name = name,
                            sets = sets,
                            reps = reps,
                            day = selectedDay
                        )
                    )
                    showAddSheet = false
                }
            )
        }
    }
}

@Composable
fun AddExerciseContent(
    availableExercises: List<ExercisesEntity>,
    onAdd: (String, Int, String) -> Unit
) {
    var selectedExercise by remember { mutableStateOf<ExercisesEntity?>(null) }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id=R.string.choose_exercise), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (availableExercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableExercises.size) { index ->
                    val exercise = availableExercises[index]
                    val isSelected = selectedExercise?.id == exercise.id

                    Surface(
                        onClick = {
                            selectedExercise = exercise
                            sets = exercise.sets.toString()
                            reps = exercise.reps
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(exercise.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        Text(stringResource(id = R.string.workout_details), style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text(stringResource(id = R.string.sets)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text(stringResource(id = R.string.reps)) },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                selectedExercise?.let {
                    if (sets.isNotBlank() && reps.isNotBlank()) {
                        onAdd(it.name, sets.toIntOrNull() ?: 1, reps)
                    }
                }
            },
            enabled = selectedExercise != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.add_to_day))
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
fun ExerciseCard(exercise: ExercisesEntity, onComplete: (Boolean, List<String>, List<String>) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val initialWeights = if (exercise.weights.isNotEmpty()) exercise.weights.split(",") else List(exercise.sets) { "" }
    val initialReps = if (exercise.actualReps.isNotEmpty()) exercise.actualReps.split(",") else List(exercise.sets) { "" }

    val weights = remember { mutableStateListOf<String>().apply {
        addAll(initialWeights.let { if (it.size < exercise.sets) it + List(exercise.sets - it.size) { "" } else it })
    } }
    val repsDone = remember { mutableStateListOf<String>().apply {
        addAll(initialReps.let { if (it.size < exercise.sets) it + List(exercise.sets - it.size) { "" } else it })
    } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (exercise.isCompleted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
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
                    tint = if (exercise.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
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
                        text = stringResource(id = R.string.exercise_summary, exercise.sets, exercise.reps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = {
                    onComplete(!exercise.isCompleted, weights.toList(), repsDone.toList())
                }) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(id = R.string.done_exercise),
                        tint = if (exercise.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
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
                            value = weights.getOrElse(i) { "" },
                            onValueChange = { weights[i] = it },
                            label = { Text(stringResource(id = R.string.kg), fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = repsDone.getOrElse(i) { "" },
                            onValueChange = { repsDone[i] = it },
                            label = { Text(stringResource(id = R.string.reps), fontSize = 10.sp) },
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