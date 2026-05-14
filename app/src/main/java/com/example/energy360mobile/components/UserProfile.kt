package com.example.energy360mobile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energy360mobile.R
import com.example.energy360mobile.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Main entry point for the User Profile section.
 * It manages internal navigation between the profile dashboard and other sub-pages.
 *
 * @param rootNavController The NavController for the entire application.
 * @param viewModel The UserViewModel used to fetch and manage user data.
 */
@Composable
fun UserProfile(rootNavController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val internalNavController = rememberNavController()

    NavHost(
        navController = internalNavController,
        startDestination = "profile/home"
    ){
        composable("profile/home"){UserProfileDashboard(rootNavController, internalNavController)}
        composable("profile/training_page"){TrainingSession(onBack = { internalNavController.popBackStack() })}
    }
}

/**
 * Displays the user profile dashboard, including the header with profile picture and name,
 * account information, and training/document status cards.
 *
 * @param rootNavController The NavController for the entire application.
 * @param internalNavController The NavController for internal navigation within the profile section.
 * @param viewModel The UserViewModel used to access user information.
 */
@Composable
fun UserProfileDashboard(rootNavController: NavHostController, internalNavController: NavHostController, viewModel: UserViewModel = viewModel() ) {
    val userData by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Profile header section with background gradient and profile picture
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture container
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lion),
                        contentDescription = stringResource(id = R.string.profile_picture),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Display User Name (formatted)
                Text(
                    text = userData?.name?.split(" ")?.joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } } 
                        ?: stringResource(id = R.string.username),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                )
                // Display User ID code
                Text(
                    text = if (userData != null) "${stringResource(id = R.string.user_code)}: ${userData?.id}" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                )
            }
        }

        // Section containing account details and status cards
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.account_infos),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Account details card (Email, Date of Birth)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileInfoRow(Icons.Default.Email, "Email", userData?.email.toString())
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    ProfileInfoRow(Icons.Default.Cake, stringResource(id = R.string.dbo), userData?.let { convertMillisToDate(it.dob) } ?: "---")
                }
            }


            Text(
                text = stringResource(id = R.string.training) ,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            // Training related shortcuts
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.training_schedule),
                    subtitle =  "",
                    icon = Icons.Default.FitnessCenter,
                    isValid = true,
                    onClick = {
                        internalNavController.navigate("profile/training_page")
                    }
                )
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.nutrition),
                    subtitle = "",
                    icon = Icons.Default.FoodBank,
                    isValid = true
                )
            }

            Text(
                text = stringResource(id = R.string.docs),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            // Document and Membership status shortcuts
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.medical_details),
                    subtitle =  stringResource(id = R.string.end_verify) +": 12/24",
                    icon = Icons.Default.MedicalInformation,
                    isValid = true,
                )
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.membership),
                    subtitle = "Piano Gold",
                    icon = Icons.Default.VerifiedUser,
                    isValid = true
                )
            }
            
            // Logout button
            OutlinedButton(
                onClick = {
                    viewModel.removeUserData();
                    rootNavController.navigate("auth") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.logout))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * A reusable row for displaying a labeled information field with an icon.
 *
 * @param icon The icon to display on the left.
 * @param label The label text for the field.
 * @param value The value text for the field.
 */
@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * A card representing the status of a specific category (e.g., Training, Nutrition).
 *
 * @param modifier Modifier to be applied to the card.
 * @param title The main title of the card.
 * @param subtitle A small description or status text.
 * @param icon The icon representing the category.
 * @param isValid Boolean indicating if the status is valid or has issues (affects colors).
 * @param onClick Optional callback for when the card is clicked.
 */
@Composable
fun StatusCard(modifier: Modifier, title: String, subtitle: String, icon: ImageVector, isValid: Boolean, onClick: (() -> Unit)? = null) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isValid) MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
                             else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Converts a time in milliseconds to a formatted date string (dd/MM/yyyy).
 *
 * @param dateMillis The date in milliseconds.
 * @return A formatted date string, or "---" if an error occurs.
 */
fun convertMillisToDate(dateMillis: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = java.util.Date(dateMillis)
        sdf.format(date)
    } catch (e: Exception) {
        "---"
    }
}