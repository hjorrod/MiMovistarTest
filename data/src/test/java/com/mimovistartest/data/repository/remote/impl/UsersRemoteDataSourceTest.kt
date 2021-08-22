package com.mimovistartest.data.repository.remote.impl

import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.model.*
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class UsersRemoteDataSourceTest {
    private val test: UsersRemoteDataSource = UsersRemoteDataSource(object : RandomCoApi {
        override suspend fun getUsers(count: Int?): Response<UserPageDTOWrapper> {
            return Response.success(UserPageDTOWrapper())
        }
    })

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