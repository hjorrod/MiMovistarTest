package com.mimovistartest.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_list")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String,
//    @ColumnInfo(name = "name")
//    val name: String,
//    @ColumnInfo(name = "gender")
//    val gender: String,
//    @ColumnInfo(name = "phone")
//    val phone: String,
//    @ColumnInfo(name = "picture")
//    val picture: String,
//    @ColumnInfo(name = "street")
//    val street: String,
//    @ColumnInfo(name = "city")
//    val city: String,
//    @ColumnInfo(name = "state")
//    val state: String,
//    @ColumnInfo(name = "registeredDate")
//    val registeredDate: String,
    @ColumnInfo(name = "isFav")
    val isFav: Int,
    @ColumnInfo(name = "isRemoved")
    val isRemoved: Int
)