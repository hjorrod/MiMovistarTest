package com.mimovistartest.domain.usecases

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.model.*
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.model.LocationBO
import com.mimovistartest.domain.model.UserBO
import org.junit.Assert.*
import org.junit.Test

class GetUsersListUseCaseTest {
    private val test = GetUsersListUseCase(object : IUsersRepository {
        override suspend fun getUsersList(count: Int?): Result<UserPageDTO> {
            return Result.Success<UserPageDTO>(UserPageDTO(PageInfoDTO(), listOf()))
        }

        override suspend fun insert(user: UserEntity) {}

        override suspend fun getLocaleUserList(): Result<List<UserEntity>> {
            return Result.Success<List<UserEntity>>(listOf())
        }

        override suspend fun removeFav(userEmail: String) {}

    })

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
        assertEquals (expected, test.findUsersAndDelete(
            mutableListOf(userEntity),
            listOf(userBO, userBrotherBO)
        ))
    }
}