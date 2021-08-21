package com.mimovistartest.data.repository

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.model.*
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.util.Result

class UsersRepository (
    private val local: ILocalDataSource,
    private val remote: IUsersRemoteDataSource
) : IUsersRepository {
    override suspend fun getUsersList(count: Int?): Result<UserPageDTO> {
        return when (val remoteUsers = remote.getUsersList(count)) {
            is Result.Success -> Result.Success(remoteUsers.data)
            is Result.Failure -> Result.Failure(remoteUsers.error,
                remoteUsers.exception)
        }
    }

    override suspend fun insert(user: UserEntity) {
        local.insert(user)
    }

    override suspend fun getLocaleUserList(): Result<List<UserEntity>> {
        return when (val favList = local.getLocaleUserList()) {
            is Result.Success ->
                Result.Success(favList.data)
            is Result.Failure ->
                Result.Failure()
        }
    }

    override suspend fun removeFav(userEmail: String) {
        local.removeFav(userEmail)
    }
}