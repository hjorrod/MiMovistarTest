package com.mimovistartest.data.util

import com.mimovistartest.data.common.ServiceError

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Failure<T>(val error: ServiceError? = null, val exception: Exception? = null) :
        Result<T>()
}
