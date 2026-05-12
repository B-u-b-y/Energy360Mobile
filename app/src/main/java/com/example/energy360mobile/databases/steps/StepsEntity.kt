package com.example.energy360mobile.databases.steps

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepsEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeInMsecs: Long,
    val steps: Long
)