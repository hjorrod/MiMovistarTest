package com.mimovistartest.data.repository.remote

import com.mimovistartest.data.model.UserPageDTO
import com.mimovistartest.data.util.Result

interface IUsersRemoteDataSource {
    suspend fun getUsersList(count: Int?): Result<UserPageDTO>
}