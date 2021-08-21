package com.mimovistartest.data.repository

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.model.UserPageDTO
import com.mimovistartest.data.util.Result

interface IUsersRepository {
    suspend fun getUsersList(count: Int?): Result<UserPageDTO>

    //Local
    suspend fun insert(user: UserEntity)
    suspend fun getLocaleUserList(): Result<List<UserEntity>>
    suspend fun removeFav(userEmail: String)
}