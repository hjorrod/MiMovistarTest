package com.mimovistartest.data.common

import com.google.gson.annotations.SerializedName

data class ServiceError(
    @SerializedName("error")
    val errorInfo: ServiceErrorInfo = ServiceErrorInfo()
)

data class ServiceErrorInfo(
    val message: String? = null
)