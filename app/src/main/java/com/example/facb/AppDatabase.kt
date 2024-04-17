package com.example.facb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrientationData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orientationDataDao(): OrientationDataDao
}
