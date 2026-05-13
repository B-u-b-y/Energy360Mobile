package com.example.energy360mobile.databases.userData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userData")
data class UserEntity(
    @PrimaryKey
    var id: String,
    val name: String,
    val password: String,
    val email: String,
    val dob: Long
)
