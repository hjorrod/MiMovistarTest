package com.mimovistartest.domain.usecases

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.common.UseCase
import com.mimovistartest.domain.model.UserBO
import com.mimovistartest.domain.model.UserPageBO
import com.mimovistartest.domain.model.map

class GetUsersListUseCase(private val repository: IUsersRepository) :
    UseCase<GetUsersListUseCase.Params, Result<UserPageBO>>() {

    override suspend fun run(params: Params): Result<UserPageBO> {
        return when (val response = repository.getUsersList(params.count)) {
            is Result.Success -> findFavUsersAndRemoved(response.data.map())
            is Result.Failure -> Result.Failure(response.error, response.exception)
        }
    }

    /** when we receive a new random users list, we check the response and:
     *  sort the list by name
     *  mark as Fav if it corresponds
     *  delete user from the list if it was deleted by the user previously
     **/
    private suspend fun findFavUsersAndRemoved(userPageBO: UserPageBO): Result<UserPageBO> {
        //1 - Sort list by name
        userPageBO.users.sortedBy { it.name }.also { sortedList ->
            when (val response = repository.getLocaleUserList()) {
                is Result.Success -> {
                    response.data.apply {
                        //2 - Find fav users and mark them
                        forEach { localUser ->
                            sortedList.firstOrNull {
                                it.email == localUser.email && localUser.isFav == 1
                            }?.isFav = true
                        }
                        //3 - Remove removed users to the final list
                        val removedList = filter { it.isRemoved == 1 }.toMutableList()
                        
                        return Result.Success(UserPageBO(findUsersAndDelete(removedList, sortedList)))
                    }
                }
                is Result.Failure -> return Result.Success(UserPageBO(sortedList))
            }
        }
    }

    fun findUsersAndDelete(
        removedList: MutableList<UserEntity>,
        originalList: List<UserBO>
    ): MutableList<UserBO> {
        val finalList = mutableListOf<UserBO>()
        originalList.forEach { user ->
            if (!removedList.any { it.email == user.email })
                finalList.add(user)
        }
        return finalList
    }

    data class Params(
        val count: Int? = null
    )
}