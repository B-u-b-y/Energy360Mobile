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

class ExercisesViewModel(application: Application) : AndroidViewModel(application)  {

    private val repository : ExercisesRepository

    private val exerciseFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())
    val exercise: StateFlow<List<ExercisesEntity>>
        get() = exerciseFlow

    private val completedExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())
    val completedExercises: StateFlow<List<ExercisesEntity>>
        get() = completedExercisesFlow

    private val availableExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())
    val availableExercises: StateFlow<List<ExercisesEntity>>
        get() = availableExercisesFlow

    init {
        val database = ExercisesDatabase.getDatabase(application)
        val dao = database.exercisesDao()
        val firebaseDataSource = ExercisesFirebaseDataSource()

        repository = ExercisesRepository(dao,firebaseDataSource)
        viewModelScope.launch {
            repository.completedExercises
                .collect { completedExercisesFlow.value = it }
        }
    }

    fun deleteExercise() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun insertExercise(exercise: ExercisesEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
        }
    }

    fun updateExerciseCompletion(id: Int, completed: Boolean, weights: List<String>, reps: List<String>) {
        viewModelScope.launch {
            repository.updateExerciseCompletion(id, completed, weights.joinToString(","), reps.joinToString(","))
        }
    }

    fun fetchAvailableExercises() {
        viewModelScope.launch {
            val list = repository.getAvailableExercisesFromCloud()
            availableExercisesFlow.value = list
        }
    }
}
