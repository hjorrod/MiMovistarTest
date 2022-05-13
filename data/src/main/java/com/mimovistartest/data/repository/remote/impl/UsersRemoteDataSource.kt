package com.mimovistartest.data.repository.remote.impl

import android.util.Log
import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.api.RandomCoApiException
import com.mimovistartest.data.common.ServiceError
import com.mimovistartest.data.common.ServiceErrorInfo
import com.mimovistartest.data.model.UserDTO
import com.mimovistartest.data.model.UserPageDTO
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.util.Result
import javax.inject.Inject

class UsersRemoteDataSource @Inject constructor(private val randomCoApi: RandomCoApi) :
    IUsersRemoteDataSource {
    override suspend fun getUsersList(count: Int?): Result<UserPageDTO> {
        val response = randomCoApi.getUsers(count)
        Log.d(
            "randomCo",
            " response ${response.isSuccessful} - ${response.errorBody()} - ${response.body()} - ${response.message()} - $response"
        )
        if (response.isSuccessful) {
            response.body()?.let {
                return if (it.users.isEmpty())
                    Result.Failure(exception = RandomCoApiException(RandomCoApiException.EMPTY_RESULT))
                else
                    Result.Success(UserPageDTO(it.pageInfo, getUniqueUserList(it.users)))
            } ?: kotlin.run {
                return Result.Failure(exception = RandomCoApiException(RandomCoApiException.EMPTY_RESULT))
            }
        }
        return Result.Failure(
            ServiceError(ServiceErrorInfo("Fail to get random users")),
            RandomCoApiException(RandomCoApiException.UNKNOWN)
        )
    }

    fun getUniqueUserList(usersList: List<UserDTO>): List<UserDTO> {
        //Note: distinct by email because email must be unique
        return usersList.distinctBy { it.email }.toList().sortedBy { it.name.name }
    }
}