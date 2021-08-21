package com.mimovistartest.data.repository.local

import androidx.room.*
import com.mimovistartest.data.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userId: UserEntity)

    @Query("SELECT * FROM users_list")
    fun getLocaleUserList(): List<UserEntity>

    @Query("DELETE FROM users_list WHERE email = :email")
    fun removeFav(email: String)
}