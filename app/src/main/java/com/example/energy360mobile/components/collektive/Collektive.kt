package com.example.energy360mobile.components.collektive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.accumulation.countDevices
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.consensus.boundedElection
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun CollektiveNearbyDevices(modifier: Modifier) {
    val stableDeviceId = remember { loadOrCreateDeviceId() }
    var viewModel by remember { mutableStateOf<NearbyDevicesViewModel?>(null) }

    // Platform-specific location service initialization
    LocationPermissionHandler(
        onPermissionGranted = { locationService ->
            if (viewModel == null) {
                viewModel = NearbyDevicesViewModel(
                    deviceId = stableDeviceId,
                    initialMessageSettings = initialMessageSettings,
                    locationService = locationService,
                )
            }
        },
        onPermissionDenied = {
            // GPS is mandatory - app cannot function without location
            viewModel = null
        },
    )

    viewModel?.let { vm ->
//        val devices by vm.dataFlow.collectAsState()
        val connection by vm.connectionFlow.collectAsState()
//        val currentLocation by vm.currentLocationFlow.collectAsState()
//        val locationError by vm.locationErrorFlow.collectAsState()
        val uuid = vm.deviceId

        LaunchedEffect(Unit) {
            // Start location tracking first - GPS is mandatory
            vm.startLocationTracking()
            // Wait for GPS location to be available, then start Collektive program
            vm.startCollektiveProgram()
        }

        DisposableEffect(vm) {
            onDispose {
                vm.cleanup()
            }
        }

        Screen(
            modifier = modifier,
            connection,
            uuid,
            vm,
        )
    }
}




///**
// * Count the number of devices in the network.
// * The total is accumulated in the device with [localId] 0 (the [sink]).
// * Other devices hold the number of devices in their subtree towards the network edge, inlcuding themselves.
// * A leaf node (with no outward neighbors) will hold 1.
// */
//fun Aggregate<Int>.countDevicesEntrypoint(): Int = countDevices(sink = localId == 0)
//
///**
// * Broadcast from the [source] the number of devices connected to the network.
// * Requires a [distances] field to be used as metric for computing the distance from the source to the target.
// */
//fun Aggregate<Int>.broadcastCountDevices(distances: Field<Int, Double>, source: Boolean): Int = gradientCast(
//    metric = distances,
//    source = source,
//    local = countDevices(sink = source),
//)
//
///**
// * [collektiveDevice] represents the device running the Collektive program.
// * It is used to access the device's properties and methods,
// * such as the [distances] method, which returns a field of distances from the neighboring nodes.
// * In this case, the source is the device with [localId] equal to 0.
// */
//fun Aggregate<Int>.broadcastCountDevicesEntrypoint(collektiveDevice: CollektiveDevice<*>): Int = broadcastCountDevices(
//    distances = with(collektiveDevice) { distances() },
//    source = localId == 0,
//)
//
///**
// * Counts and broadcast the number of devices in the network using a leader election.
// * If the network is segmented, each connected component elects a leader to act as root.
//// * Note that [env] is used only in relation to the simulation environment.
// */
//fun Aggregate<Int>.broadcastCountDevicesWithLeaderElectionEntrypoint(
//    collektiveDevice: CollektiveDevice<*>,
//    env: EnvironmentVariables,
//): Int {
//    val isLeader = (findLeader() == localId).also { env["isSource"] = it }
//    return broadcastCountDevices(
//        distances = with(collektiveDevice) { distances() },
//        source = isLeader,
//    )
//}
//
///**
// * Elects a leader in the network of devices within a bounded space.
// */
//fun Aggregate<Int>.findLeader(): Int = boundedElection(bound = 25)
//
///**
// * --- MODULAR EXAMPLE: NEIGHBORHOOD STEP CHALLENGE ---
// * These functions demonstrate how to use aggregate programming to create
// * decentralized, location-aware community features.
// */
//
///**
// * Specifically counts devices within a 100m range.
// * This filters the network to a local radius.
// */
//fun Aggregate<Int>.countDevicesWithin100m(distances: Field<Int, Double>): Int {
//    // 1. Get the values of all neighbors (each device contributes 1)
//    val neighboringDevices = neighboring(1)
//
//    // 2. Filter neighbors that are within 100 meters
//    val devicesInRange = neighboringDevices.neighbors.sequence
//        .filter { entry ->
//            // Access the distance for this neighbor ID
//            // We use 'distances' field which is aligned with 'neighboringDevices'
//            val dist = distances[entry.id]
//            dist <= 100.0
//        }
//        .map { it.value }
//        .sum()
//
//    // 3. Total is Local (1) + Neighbors in range
//    return 1 + devicesInRange
//}
//
///**
// * Specifically sums the steps of all users within a 100m range.
// * Can be used for a "Live Neighborhood Leaderboard" or community challenge.
// */
//fun Aggregate<Int>.sumStepsWithin100m(
//    localSteps: Int,
//    distances: Field<Int, Double>
//): Int {
//    // 1. Share local steps with neighbors
//    val neighboringSteps = neighboring(localSteps)
//
//    // 2. Sum steps from neighbors within 100 meters
//    val stepsInRange = neighboringSteps.neighbors.sequence
//        .filter { entry -> distances[entry.id] <= 100.0 }
//        .map { it.value }
//        .sum()
//
//    // 3. Return total (Local steps + nearby neighbors' steps)
//    return localSteps + stepsInRange
//}
//
///**
// * Entrypoint example for the Step Challenge.
// * Integrates with the CollektiveDevice to get live distance data and local step count.
// */
//fun Aggregate<Int>.neighborhoodStepChallengeEntrypoint(
//    collektiveDevice: CollektiveDevice<*>,
//    localSteps: Int
//): Int = sumStepsWithin100m(
//    localSteps = localSteps,
//    distances = with(collektiveDevice) { distances() }
//)
