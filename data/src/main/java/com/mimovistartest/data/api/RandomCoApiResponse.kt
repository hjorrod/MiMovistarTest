package com.mimovistartest.data.api

import com.google.gson.annotations.SerializedName
import com.mimovistartest.data.model.PageInfoDTO

abstract class RCApiPageResponse<T> {
    @SerializedName("info")
    val pageInfo: PageInfoDTO = PageInfoDTO()
    @SerializedName("results")
    val users: List<T> = listOf()
}