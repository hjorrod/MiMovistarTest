package com.mimovistartest.domain.di

import com.mimovistartest.domain.usecases.*
import org.koin.dsl.module

val useCaseModule = module {
    // Generic use case
    factory { GetUsersListUseCase(get()) }
    factory { AddUserDBUseCase(get()) }
    factory { RemoveUserDBUseCase(get()) }
}