package com.mimovistartest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationVO(
    val street: String,
    val city: String,
    val state: String
): Parcelable