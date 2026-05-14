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
import androidx.core.content.edit

/**
 * ViewModel responsible for managing and processing daily step count data.
 * It interacts with the [StepsRepository] to store data locally and handles the logic
 * for calculating "steps today" based on the system's cumulative step counter sensor.
 *
 * @param application The application context.
 */
class StepsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StepsRepository

    private val prefs = application.getSharedPreferences(
        "steps_prefs",
        Application.MODE_PRIVATE
    )

    /**
     * A StateFlow emitting the most recent [StepsEntity] from the database.
     * Represents the current total of steps calculated for the day.
     */
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

    /**
     * Processes the raw step count received from the [android.hardware.Sensor.TYPE_STEP_COUNTER].
     * Since the system sensor provides the total steps since the last reboot, this function
     * maintains a "baseline" to calculate the number of steps taken specifically today.
     *
     * @param totalSteps The cumulative step count value provided by the hardware sensor.
     */
    fun processSensorSteps(totalSteps: Long) {
        viewModelScope.launch {

            val today = LocalDate.now().toString()
            val savedDate = prefs.getString("saved_date", "")

            var baseline = prefs.getLong("baseline", -1)

            // If the day has changed, reset the baseline to the current totalSteps
            // so that "today's steps" (totalSteps - baseline) starts from zero again.
            if (savedDate != today) {
                baseline = totalSteps

                prefs.edit {
                    putString("saved_date", today)
                        .putLong("baseline", baseline)
                }
            }

            // Handle the very first time the app receives a sensor event
            if (baseline == -1L) {
                baseline = totalSteps

                prefs.edit {
                    putLong("baseline", baseline)
                        .putString("saved_date", today)
                }
            }

            // Calculate steps taken today
            val currentSteps = totalSteps - baseline

            val stepsEntity = StepsEntity(
                timeInMsecs = System.currentTimeMillis(),
                steps = currentSteps
            )

            // Persistence of the updated step count
            repository.insertSteps(stepsEntity)
        }
    }
}