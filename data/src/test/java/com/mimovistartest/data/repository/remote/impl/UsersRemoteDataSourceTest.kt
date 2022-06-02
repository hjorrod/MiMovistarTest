package com.mimovistartest.data.repository.remote.impl

import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.model.*
import com.mimovistartest.data.repository.UsersRepository
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UsersRemoteDataSourceTest {
    //Test subject
    private lateinit var usersRemoteDataSource: UsersRemoteDataSource
    //Collaborators
    private lateinit var randomCoApi: RandomCoApi

    @Before
    fun setUp(){
        randomCoApi = mock()
        usersRemoteDataSource = UsersRemoteDataSource(randomCoApi)
    }

    @Test
    fun testGetUniqueUserList() {
        val userDTO = UserDTO(
            "male",
            UserName("Jorge", "Herrera"),
            "jorgerhr86@gmail.com",
            "123456",
            UserPicture("urlImage"),
            UserLocation(LocationStreet(1, "street"), "city", "state"),
            UserRegisteredInfo("22-08-2021")
        )
        val expected = listOf(userDTO)
        assertEquals (expected, usersRemoteDataSource.getUniqueUserList(listOf(userDTO, userDTO)))
    }
}