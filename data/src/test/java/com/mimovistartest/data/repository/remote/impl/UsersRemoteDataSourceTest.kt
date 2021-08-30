package com.mimovistartest.data.repository.remote.impl

import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.model.*
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Test

class UsersRemoteDataSourceTest {
    private val api: RandomCoApi = mock()
    private val test: UsersRemoteDataSource = UsersRemoteDataSource(api)

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
        assertEquals (expected, test.getUniqueUserList(listOf(userDTO, userDTO)))
    }
}