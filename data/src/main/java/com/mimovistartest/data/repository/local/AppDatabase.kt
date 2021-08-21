package com.mimovistartest.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mimovistartest.data.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)

@TypeConverters
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}