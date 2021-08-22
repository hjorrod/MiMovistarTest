package com.mimovistartest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserVO (
    val id: Int,
    val gender: String,
    val name: String,
    val email: String,
    val phone: String,
    val picture: String,
    val location: LocationVO,
    val registeredDate: String,
    var isFav: Boolean = false
): Parcelable