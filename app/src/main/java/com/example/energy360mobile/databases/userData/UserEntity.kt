package com.example.energy360mobile.databases.userData

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a user in the local database.
 *
 * @property id The unique identifier of the user.
 * @property name The full name of the user.
 * @property password The user's password.
 * @property email The user's email address.
 * @property dob The user's date of birth represented in milliseconds.
 */
@Entity(tableName = "userData")
data class UserEntity(
    @PrimaryKey
    var id: String,
    val name: String,
    val password: String,
    val email: String,
    val dob: Long
)
