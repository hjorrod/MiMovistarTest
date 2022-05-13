package com.mimovistartest.data.repository.local.impl


import com.mimovistartest.data.entities.UserEntity
import com.mimovistartest.data.repository.local.UserDao
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.util.Result
import javax.inject.Inject

class LocalDataSource @Inject constructor (
    private val userDao: UserDao
) : ILocalDataSource {
    override fun getLocaleUserList(): Result<List<UserEntity>> {
        val favList = userDao.getLocaleUserList()
        return if (favList.isNotEmpty())
            Result.Success(favList)
        else
            Result.Failure()
    }

    override fun insert(user: UserEntity) {
        userDao.insert(user)
    }

    override fun removeFav(userEmail: String) {
        userDao.removeFav(userEmail)
    }
}