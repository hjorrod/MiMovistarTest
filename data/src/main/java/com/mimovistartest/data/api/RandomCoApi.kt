package com.mimovistartest.data.api

import com.mimovistartest.data.model.UserPageDTOWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomCoApi {

    @GET(".")
    suspend fun getUsers(
        @Query ("results") page: Int? = null): Response<UserPageDTOWrapper>

}