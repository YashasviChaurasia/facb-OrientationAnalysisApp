package com.example.facb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orientation_data")
data class OrientationData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roll: Float,
    val pitch: Float,
    val yaw: Float,
    val timestamp: Long
)

