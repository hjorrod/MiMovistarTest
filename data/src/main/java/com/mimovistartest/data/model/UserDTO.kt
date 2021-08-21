package com.mimovistartest.data.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
    val gender: String,
    val name: UserName,
    val email: String,
    val phone: String,
    val picture: UserPicture,
    val location: UserLocation,
    val registered: UserRegisteredInfo
)

data class UserName(
    @SerializedName("first")
    val name: String,
    @SerializedName("last")
    val surname: String
)

data class UserPicture(
    @SerializedName("medium")
    val url: String
)

data class UserLocation(
    val street: LocationStreet,
    val city: String,
    val state: String
)

data class LocationStreet(
    val number: Int,
    val name: String
)

data class UserRegisteredInfo (
    val date: String
)