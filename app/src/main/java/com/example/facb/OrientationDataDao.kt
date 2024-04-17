package com.example.facb

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface OrientationDataDao {
    @Insert
    suspend fun insertData(orientationData: OrientationData)
}
