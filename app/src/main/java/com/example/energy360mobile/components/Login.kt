package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energy360mobile.R
import com.example.energy360mobile.components.noConnection.LoginNoConnection
import com.example.energy360mobile.components.noConnection.RegisterNoConnection
import com.example.energy360mobile.databases.userData.UserEntity
import com.example.energy360mobile.utils.isNetworkAvailable
import com.example.energy360mobile.viewmodels.UserViewModel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.testTag
import com.example.energy360mobile.utils.ConnectivityObserver
import com.example.energy360mobile.utils.NetworkObserver
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Entry point for the Authentication flow.
 * Manages the navigation between Login and Register screens and monitors network connectivity
 * to display "No Connection" screens when necessary.
 *
 * @param viewModel The UserViewModel used for authentication logic.
 * @param rootNavController The main NavController of the application.
 */
@Composable
fun Execution(viewModel: UserViewModel, rootNavController: NavHostController) {
    val innerNavController = rememberNavController()
    val context = LocalContext.current

    val connectivityObserver = remember { ConnectivityObserver(context) }
    val status by connectivityObserver.observe().collectAsState(
        initial = if (isNetworkAvailable(context)) NetworkObserver.Status.Available
        else NetworkObserver.Status.Unavailable
    )


    NavHost(
        navController = innerNavController,
        startDestination = "login"
    ) {
        composable("login") {
            if (status == NetworkObserver.Status.Available) {
                Login(viewModel, rootNavController, innerNavController)
            } else {
                LoginNoConnection()
            }
        }
        composable("register") {
            if (status == NetworkObserver.Status.Available) {
                Register(viewModel, rootNavController, innerNavController)
            } else {
                RegisterNoConnection()
            }
        }
    }
}

/**
 * Displays the Login screen.
 * Allows users to input their email and password to access the application.
 *
 * @param viewModel The UserViewModel used to check credentials.
 * @param rootNavController The main NavController for navigating to the Home screen upon success.
 * @param innerNavController The local NavController for switching to the Register screen.
 */
@Composable
fun Login(viewModel: UserViewModel, rootNavController: NavHostController, innerNavController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showInvalid by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with branding and welcome message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 64.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Energy360",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        fontSize = 40.sp
                    )
                )
                Text(
                    text = stringResource(id = R.string.welcome_login),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Input fields and Login action
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(
                        modifier = Modifier.testTag("passwordVisibility"),
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(image, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("passwordField"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error message for invalid credentials
            if (showInvalid) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.invalid_credentials),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        val isSuccess = viewModel.checkCredentials(email.lowercase(), password)
                        if (isSuccess) {
                            // On success, navigate to Home and clear auth stack
                            rootNavController.navigate(Screen.Home.route) {
                                popUpTo("auth") { inclusive = true }
                            }
                        } else {
                            showInvalid = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { innerNavController.navigate("register") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(id = R.string.not_account))
            }
        }
    }
}

/**
 * Displays the Register screen.
 * Allows new users to create an account by providing their name, email, password, and date of birth.
 *
 * @param viewModel The UserViewModel used for user registration.
 * @param rootNavController The main NavController for navigating to the Home screen upon success.
 * @param innerNavController The local NavController for switching back to the Login screen.
 */
@Composable
fun Register(viewModel: UserViewModel, rootNavController: NavHostController, innerNavController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var dob by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()
    var showInvalid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with "Create Account" title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(bottomEnd = 64.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.create_account) ,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Registration form fields
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.full_name))},
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Date of Birth selection (read-only field with picker)
            OutlinedTextField(
                value = dob,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.dbo)) },
                leadingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                singleLine = true
            )

            // Date validation error message
            if (dateError) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.invalid_dob),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                )
            }

            // Material 3 Date Picker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val selectedDate = datePickerState.selectedDateMillis
                            if (selectedDate != null) {
                                // Convert millis to readable string for the TextField
                                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                dob = sdf.format(Date(selectedDate))
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Error message for existing user or registration failure
            if (showInvalid) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.existing_user),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null && validateAge(selectedDateMillis)) {
                        dateError = false
                        // Prepare user entity with formatted name
                        val userData = UserEntity(
                            id = "",
                            name = name.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                            email = email.lowercase(),
                            password = password,
                            dob = selectedDateMillis
                        )
                        scope.launch {
                            val isSuccess = viewModel.registerUser(userData)
                            if (!isSuccess) {
                                rootNavController.navigate(Screen.Home.route)
                            }else {
                                showInvalid = true
                            }
                        }
                    } else {
                        dateError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(stringResource(id = R.string.register), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = {
                    innerNavController.navigate("login") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(id = R.string.already_account   ))
            }
        }
    }
}

/**
 * Validates that the user is between 16 and 100 years old based on their birth date.
 *
 * @param birthDateMillis The birth date in milliseconds.
 * @return True if the age is valid, false otherwise.
 */
fun validateAge(birthDateMillis: Long): Boolean {
    return try {
        val birthDate = Instant.ofEpochMilli(birthDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val now = LocalDate.now()
        val age = Period.between(birthDate, now).years
        age in 16..100
    } catch (e: Exception) {
        false
    }
}