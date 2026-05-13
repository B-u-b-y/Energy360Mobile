package com.example.energy360mobile

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.energy360mobile.components.BottomBar
import com.example.energy360mobile.components.ChatDetailScreen
import com.example.energy360mobile.components.Execution
import com.example.energy360mobile.components.HomeScreen
import com.example.energy360mobile.components.MessagesScreen
import com.example.energy360mobile.components.NavGraph
import com.example.energy360mobile.components.ProfileScreen
import com.example.energy360mobile.components.ReservationScreen
import com.example.energy360mobile.components.Screen
import com.example.energy360mobile.components.SearchScreen
import com.example.energy360mobile.components.ServicesPage
import com.example.energy360mobile.components.TrainingSession
import com.example.energy360mobile.theme.Energy360MobileTheme
import com.example.energy360mobile.utils.isNetworkAvailable
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.ServicesViewModel
import com.example.energy360mobile.viewmodels.StepsViewModel
import com.example.energy360mobile.viewmodels.UserViewModel

class MainActivity : ComponentActivity(), SensorEventListener{

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private val stepsViewModel: StepsViewModel by viewModels()

    private val permissions = arrayOf(
        android.Manifest.permission.BODY_SENSORS,
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            android.Manifest.permission.BODY_SENSORS
        }
    ).distinct().toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        ActivityCompat.requestPermissions(this, permissions, 1001)
        enableEdgeToEdge()
        setContent {
            Energy360MobileTheme {
                MainApp(stepsViewModel)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val totalSteps = it.values[0].toLong()
            stepsViewModel.processSensorSteps(totalSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()

        stepSensor?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun MainApp(stepsViewModel : StepsViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userViewModel : UserViewModel = viewModel()
    val serviceViewModel : ServicesViewModel = viewModel()
    val reservationsViewModel : ReservationsViewModel = viewModel()

    val userData by userViewModel.userData.collectAsState()

    val startingDestination: String = if (userData != null) Screen.Home.route else "auth"

    val mainScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Reservation.route,
        Screen.Profile.route,
        Screen.Messages.route,
        "training_page",
        "services"
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in mainScreens) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            paddingValues = if (currentRoute in mainScreens) innerPadding else PaddingValues(0.dp),
            startingDestination = startingDestination,
            userViewModel = userViewModel,
            stepsViewModel = stepsViewModel,
            reservationsViewModel = reservationsViewModel
        )
    }
}