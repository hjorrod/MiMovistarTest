package com.mimovistartest.domain.usecases

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.model.*
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.data.repository.UsersRepository
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.model.LocationBO
import com.mimovistartest.domain.model.UserBO
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetUsersListUseCaseTest {
    //Test subject
    private lateinit var getUserListUseCase: GetUsersListUseCase
    //Collaborators
    private lateinit var userRepository: UsersRepository
    private lateinit var userDao: ILocalDataSource
    private lateinit var userApi: IUsersRemoteDataSource

    @Before
    fun setUp(){
        userDao = mock()
        userApi = mock()
        userRepository = UsersRepository(userDao, userApi)
        getUserListUseCase = GetUsersListUseCase(userRepository)
    }

    @Test
    fun testFindUsersAndDelete() {
        val userEntity = UserEntity("jorgerhr86@gmail.com", 0, 1)
        val userBO = UserBO(
            "male",
            "Jorge Herrera",
            "jorgerhr86@gmail.com",
            "123456",
           "urlImage",
            LocationBO("street, 1", "city", "state"),
            "22-08-2021"
        )
        val userBrotherBO = UserBO(
            "male",
            "Sergio Herrera",
            "sergiohr81@gmail.com",
            "123456",
            "urlImage",
            LocationBO("street, 1", "city", "state"),
            "22-08-2021"
        )
        val expected = listOf(userBrotherBO)
        assertEquals (expected, getUserListUseCase.findUsersAndDelete(
            mutableListOf(userEntity),
            listOf(userBO, userBrotherBO)
        ))
    }
}