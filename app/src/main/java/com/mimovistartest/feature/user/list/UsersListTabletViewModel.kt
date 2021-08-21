package com.mimovistartest.feature.user.list

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mimovistartest.base.BaseViewModel
import com.mimovistartest.data.api.RandomCoApiException
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.usecases.AddUserDBUseCase
import com.mimovistartest.domain.usecases.GetUsersListUseCase
import com.mimovistartest.domain.usecases.RemoveUserDBUseCase
import com.mimovistartest.model.UserVO
import com.mimovistartest.model.joinList
import com.mimovistartest.model.map

class UsersListTabletViewModel(
    private val getUsersListUseCase: GetUsersListUseCase
): BaseViewModel() {

    private val _usersList by lazy { MutableLiveData<List<UserVO>>() }
    val usersList: MutableLiveData<List<UserVO>> = _usersList

    private val _favList by lazy { MutableLiveData<List<UserVO>>() }
    val favList: MutableLiveData<List<UserVO>> = _favList

    init {
        loadUsers()
    }

    private fun loadUsers() {
        getUsersListUseCase.invoke(
            scope = viewModelScope,
            params = GetUsersListUseCase.Params(40)
        ){ result ->
            when (result) {
                is Result.Success -> {
                    _usersList.value = result.data.users.map()
                    loadFavUsers()
                }
                is Result.Failure -> {
                    error.value = ""
                }
            }
        }
    }

    private fun loadFavUsers() {
        _favList.value = _usersList.value?.filter { it.isFav }
    }

}