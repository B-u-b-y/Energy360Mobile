package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.exercises.ExercisesDatabase
import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.example.energy360mobile.databases.exercises.ExercisesRepository
import com.example.energy360mobile.databases.exercises.ExercisesFirebaseDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing training exercises.
 * It handles the synchronization between the local [ExercisesRepository] and the UI,
 * allowing users to track their workout progress, mark exercises as complete,
 * and fetch new exercises from the cloud.
 *
 * @param application The application instance.
 */
class ExercisesViewModel(application: Application) : AndroidViewModel(application)  {

    private val repository : ExercisesRepository

    // Internal flow for user's assigned exercises
    private val exerciseFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())

    /**
     * A StateFlow emitting the list of exercises assigned to the user for the current week.
     */
    val exercise: StateFlow<List<ExercisesEntity>>
        get() = exerciseFlow

    // Internal flow for exercises marked as completed
    private val completedExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())

    /**
     * A StateFlow emitting the list of exercises that the user has marked as completed.
     */
    val completedExercises: StateFlow<List<ExercisesEntity>>
        get() = completedExercisesFlow

    // Internal flow for exercises available to be added from the cloud
    private val availableExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())

    /**
     * A StateFlow emitting the list of exercises fetched from the cloud that are available to be added.
     */
    val availableExercises: StateFlow<List<ExercisesEntity>>
        get() = availableExercisesFlow

    init {
        val database = ExercisesDatabase.getDatabase(application)
        val dao = database.exercisesDao()
        val firebaseDataSource = ExercisesFirebaseDataSource()

        repository = ExercisesRepository(dao,firebaseDataSource)
        viewModelScope.launch {
            repository.allExercises.collect {
                exerciseFlow.value = it
            }
        }
    }

    /**
     * Clears all exercises from the local database.
     */
    fun deleteExercise() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    /**
     * Inserts a new exercise into the local database.
     *
     * @param exercise The [ExercisesEntity] to be inserted.
     */
    fun insertExercise(exercise: ExercisesEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
        }
    }

    /**
     * Updates the completion status and performance data (weights and reps) of an exercise.
     *
     * @param id The unique identifier of the exercise.
     * @param completed Whether the exercise is marked as done.
     * @param weights List of weights used for each set.
     * @param reps List of repetitions performed for each set.
     */
    fun updateExerciseCompletion(id: Int, completed: Boolean, weights: List<String>, reps: List<String>) {
        viewModelScope.launch {
            repository.updateExerciseCompletion(id, completed, weights.joinToString(","), reps.joinToString(","))
        }
    }

    /**
     * Fetches the list of available exercises from the remote cloud source (Firebase).
     * Updates the [availableExercises] StateFlow upon completion.
     */
    fun fetchAvailableExercises() {
        viewModelScope.launch {
            val list = repository.getAvailableExercisesFromCloud()
            availableExercisesFlow.value = list
        }
    }
}
