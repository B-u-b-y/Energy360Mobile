package com.example.energy360mobile.viewmodels

import android.app.Application
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.userData.UserDatabase
import com.example.energy360mobile.databases.userData.UserEntity
import com.example.energy360mobile.databases.userData.UserRepository
import com.example.energy360mobile.databases.userData.UserFirebaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel that handles user-related business logic, bridging the UI and the data layer (Repository).
 * It manages user authentication, profile data synchronization, and local storage.
 *
 * @param application The application instance used to initialize the local database.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val prefs = application.getSharedPreferences("user_prefs", Application.MODE_PRIVATE)

    // Internal flow handling the subscription to user data changes
    private val userDataFlow: StateFlow<UserEntity?>

    private val _profileImageUri = MutableStateFlow(prefs.getString("profile_image_uri", null))

    /**
     * A StateFlow emitting the current profile image URI (local file path).
     * Can be null if no profile image is set.
     */
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    /**
     * A StateFlow emitting the current user data from the local database.
     * Can be null if no user is logged in.
     */
    val userData: StateFlow<UserEntity?>
        get() = userDataFlow

    init {
        val database = UserDatabase.getDatabase(application)
        val dao = database.userDao()
        val firebaseDataSource = UserFirebaseDataSource()
        repository = UserRepository(dao,firebaseDataSource)

        // Initializes userData
        userDataFlow = repository.user.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Starts with null until the database is reached
        )
    }

    /**
     * Clears the current user data from the local database and preferences.
     */
    fun removeUserData(){
        viewModelScope.launch {
            repository.removeUser()
            saveProfileImageUri(null)
        }
    }

    /**
     * Saves the profile image URI. If a remote URI is provided (e.g., from the photo picker),
     * it copies the image to internal storage to ensure it remains accessible after app restarts.
     *
     * @param uri The URI of the profile image to save. Can be null to clear the image.
     */
    fun saveProfileImageUri(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri == null) {
                _profileImageUri.value = null
                prefs.edit { putString("profile_image_uri", null) }
                // Optionally delete the local file if it exists
                val localFile = File(getApplication<Application>().filesDir, "profile_image.jpg")
                if (localFile.exists()) localFile.delete()
                return@launch
            }

            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val localFile = File(context.filesDir, "profile_image.jpg")

                inputStream?.use { input ->
                    FileOutputStream(localFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val internalUri = Uri.fromFile(localFile).toString()
                val uniqueUri = "$internalUri?t=${System.currentTimeMillis()}"
                withContext(Dispatchers.Main) {
                    _profileImageUri.value = uniqueUri
                    prefs.edit {
                        putString("profile_image_uri", uniqueUri)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Registers a new user. Checks if the email already exists before proceeding to
     * save the user data both locally and in the cloud.
     *
     * @param userData The UserEntity containing the information of the user to register.
     * @return True if the email already exists (registration failed), false otherwise.
     */
    suspend fun registerUser(userData: UserEntity) : Boolean{
        return withContext(Dispatchers.IO) {
            val emailExists = repository.checkIfEmailExists(userData)
            if (!emailExists) {
                repository.insertUser(userData)
            }
            emailExists
        }
    }

    /**
     * Validates user credentials against the remote data source.
     * If valid, the user data is cached in the local database.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return True if the credentials are valid and login is successful, false otherwise.
     */
    suspend fun checkCredentials(email: String, password: String): Boolean{
        return withContext(Dispatchers.IO) {
            val fetchedUser = repository.fetchUserData(email)
            if (fetchedUser != null && fetchedUser.email == email && fetchedUser.password == password) {
                repository.insertUser(fetchedUser)
                true
            } else {
                false
            }
        }
    }
}