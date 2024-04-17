package com.example.facb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrientationDataDao {
    @Insert
    suspend fun insertData(orientationData: OrientationData)

    @Query("SELECT * FROM orientation_data ORDER BY timestamp DESC LIMIT 50")
    fun getAllOrientationData(): List<OrientationData>
}
