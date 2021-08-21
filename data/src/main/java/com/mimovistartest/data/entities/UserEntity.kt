package com.mimovistartest.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_list")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "isFav")
    val isFav: Int,
    @ColumnInfo(name = "isRemoved")
    val isRemoved: Int
)