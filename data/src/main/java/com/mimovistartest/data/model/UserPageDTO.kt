package com.mimovistartest.data.model

import com.mimovistartest.data.api.RCApiPageResponse

class UserPageDTOWrapper : RCApiPageResponse<UserDTO>()

class UserPageDTO (
    val info: PageInfoDTO,
    val users: List<UserDTO>
)