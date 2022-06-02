package com.mimovistartest.data.repository

import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.model.*
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.util.Result
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class UsersRepositoryTest {
    //Test subject
    private lateinit var userRepository: UsersRepository
    //Collaborators
    private lateinit var userDao: ILocalDataSource
    private lateinit var userApi: IUsersRemoteDataSource

    //Utilities
    lateinit var userFromApi: Result<UserPageDTO>
    lateinit var userFromDao: Result<List<UserEntity>>

    private var forceEmptyResult = false

    @Before
    fun setUp(){
        //Mocking UserApi
        userApi = mock()
        userFromApi = Result.Success(
            UserPageDTO(
                PageInfoDTO(),
                listOf(
                    UserDTO(
                        "male",
                        UserName("Jorge", "Herrera"),
                        "email",
                        "phone",
                        UserPicture("url"),
                        UserLocation(LocationStreet(1,"street"), "city", "state"),
                        UserRegisteredInfo("date")
                    )
                )
            )
        )
        runBlocking {
            whenever(userApi.getUsersList(any())).thenAnswer {
                if (forceEmptyResult) Result.Failure()
                else userFromApi
            }
        }

        //Mocking UserDao
        userDao = mock()
        userFromDao = Result.Success(
            listOf(UserEntity("email", 1, 0))
        )
        whenever(userDao.getLocaleUserList()).thenAnswer {
            if (forceEmptyResult) Result.Failure()
            else userFromDao
        }

        userRepository = UsersRepository(userDao, userApi)
    }

    /**
     * Cuando pedimos la lista de usuarios:
     * - La pedimos al servidor
     * - Chequeamos si tenemos un usuario FAV almacenado en la DB y lo marcamos como FAV
     * - Chequeamos si esta eliminado ese usuario y lo eliminamos de la lista final
     */

    @Test
    fun repositoryAskAlwaysToApi(){
        runBlocking {
            userRepository.getUsersList(any()).collect()
            verify(userApi, times(1)).getUsersList(any())
        }
    }

    @Test
    fun daoIsNotCalledWhenApiReturnsUsersList() {
        runBlocking {
            userRepository.getUsersList(any()).collect()
            verify(userDao, never()).getLocaleUserList()
        }
    }

    @Test
    fun isUserRemovedFromDao() {
        runBlocking {
            val userTest = UserEntity("emailPrueba", 1, 0)
            userRepository.insert(userTest)
            userRepository.getLocaleUserList().apply {
                if (this is Result.Success)
                    this.data.firstOrNull { it.email == userTest.email  }?.let {
                        userRepository.removeFav(userTest.email)
                    }
            }
            userRepository.getLocaleUserList().apply {
                if (this is Result.Success)
                    assertEquals(true, !this.data.any { it.email == "emailPrueba" })
            }
        }
    }

    @Test
    fun isUserInsertedInDao() {
        runBlocking {
            val userTest = UserEntity("emailPrueba", 1, 0)
            userRepository.insert(userTest)
            userRepository.getLocaleUserList().apply {
                if (this is Result.Success)
                    assertEquals(true, this.data.any { it.email == "emailPrueba" })
            }
        }
    }

   @InternalCoroutinesApi
   @Test
   fun notErrorWhenListIsEmptyFromApi() {
       forceEmptyResult = true
       runBlocking {
           userRepository.getUsersList(any()).collect { result ->
               assertEquals(true, result is Result.Failure)
           }
       }
   }

    @Test
    fun notErrorWhenListIsEmptyFromDao() {
        forceEmptyResult = true
        runBlocking {
            val result = userRepository.getLocaleUserList()
            assertEquals(true, result is Result.Failure)
        }
    }

    @After
    fun tearDown() {
        forceEmptyResult = false
    }
}