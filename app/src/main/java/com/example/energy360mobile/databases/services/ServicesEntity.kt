package com.example.energy360mobile.databases.services

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a service offered by Energy360.
 * This class is used for both database storage and UI visualization.
 */
@Entity(tableName = "services")
data class ServicesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val duration: String
)
