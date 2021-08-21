package com.mimovistartest.model

import com.mimovistartest.data.model.UserLocation
import com.mimovistartest.domain.model.LocationBO
import com.mimovistartest.domain.model.UserBO

fun MutableList<UserVO>?.joinList(newList: List<UserVO>): MutableList<UserVO> {
    return if (this == null) newList.toMutableList()
    else {
        this.addAll(newList)
        this
    }
}

fun List<UserBO>.map(): List<UserVO>{
    return this.map { UserVO(it.gender, it.name, it.email, it.phone, it.picture, it.location.map(), it.registeredDate, it.isFav) }
}

fun LocationBO.map(): LocationVO {
    return LocationVO(this.street, this.city, this.state)
}