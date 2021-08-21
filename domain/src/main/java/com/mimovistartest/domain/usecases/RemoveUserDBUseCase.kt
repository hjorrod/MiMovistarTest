package com.mimovistartest.domain.usecases

import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.domain.common.UseCaseNoResult

class RemoveUserDBUseCase(private val repository: IUsersRepository)
    : UseCaseNoResult<RemoveUserDBUseCase.Params>(){

    override suspend fun run(params: Params) {
        repository.removeFav(params.userEmail)
    }

    data class Params(
        val userEmail: String
    )
}