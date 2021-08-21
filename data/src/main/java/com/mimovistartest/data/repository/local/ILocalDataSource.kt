package com.mimovistartest.data.repository.local

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.util.Result

interface ILocalDataSource {
    fun getLocaleUserList(): Result<List<UserEntity>>
    fun insert(user: UserEntity)
    fun removeFav(userEmail: String)
}