package com.mimovistartest.domain.model

import com.mimovistartest.data.model.UserDTO
import com.mimovistartest.data.model.UserLocation
import com.mimovistartest.data.model.UserPageDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mimovistartest.data.util.Result

fun UserPageDTO.map(): UserPageBO {
    return UserPageBO(
        this.users.map()
    )
}

fun Flow<Result<UserPageDTO>>.map(): Flow<Result<UserPageBO>> {
    return this.map {
        when (it) {
            is Result.Success -> Result.Success(it.data.map())
            is Result.Failure -> Result.Failure()
        }
    }
}

fun List<UserDTO>.map(): List<UserBO> {
    return this.map {
        UserBO(
            it.gender,
            "${it.name.name} ${it.name.surname}",
            it.email,
            it.phone,
            it.picture.url,
            it.location.map(),
            it.registered.date.split("T").firstOrNull() ?: "Unknown"
        )
    }
}

fun UserLocation.map(): LocationBO {
    return LocationBO("${this.street.name}, ${this.street.number}", this.city, this.state)
}