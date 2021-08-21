package com.mimovistartest.domain.model

data class UserBO (
    val gender: String,
    val name: String,
    val email: String,
    val phone: String,
    val picture: String,
    val location: LocationBO,
    val registeredDate: String,
    var isFav: Boolean = false
)