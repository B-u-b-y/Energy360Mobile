package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.steps.StepsDatabase
import com.example.energy360mobile.databases.steps.StepsEntity
import com.example.energy360mobile.databases.steps.StepsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StepsRepository

    private val prefs = application.getSharedPreferences(
        "steps_prefs",
        Application.MODE_PRIVATE
    )

    val steps: StateFlow<StepsEntity?>

    init {
        val database = StepsDatabase.getDatabase(application)
        val dao = database.stepsDao()
        repository = StepsRepository(dao)

        steps = repository.latestSteps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun processSensorSteps(totalSteps: Long) {
        viewModelScope.launch {

            val today = LocalDate.now().toString()
            val savedDate = prefs.getString("saved_date", "")

            var baseline = prefs.getLong("baseline", -1)

            // reset baseline every new day
            if (savedDate != today) {
                baseline = totalSteps

                prefs.edit()
                    .putString("saved_date", today)
                    .putLong("baseline", baseline)
                    .apply()
            }

            // first app launch
            if (baseline == -1L) {
                baseline = totalSteps

                prefs.edit()
                    .putLong("baseline", baseline)
                    .putString("saved_date", today)
                    .apply()
            }

            val currentSteps = totalSteps - baseline

            val stepsEntity = StepsEntity(
                timeInMsecs = System.currentTimeMillis(),
                steps = currentSteps
            )

            repository.insertSteps(stepsEntity)
        }
    }
}