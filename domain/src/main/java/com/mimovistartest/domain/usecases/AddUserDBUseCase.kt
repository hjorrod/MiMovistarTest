package com.mimovistartest.domain.usecases

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.domain.common.UseCaseNoResult

class AddUserDBUseCase(private val repository: IUsersRepository)
    : UseCaseNoResult<AddUserDBUseCase.Params>(){

    override suspend fun run(params: Params) = with(params) {
        repository.insert(UserEntity(userEmail, isFav, isRemoved))
    }

    data class Params(
        val userEmail: String,
        val isFav: Int,
        val isRemoved: Int
    )
}