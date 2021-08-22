package com.mimovistartest.feature.user.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mimovistartest.base.BaseViewModel
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.usecases.GetUsersListUseCase
import com.mimovistartest.model.UserVO
import com.mimovistartest.model.map

class UsersListTabletViewModel(
    private val getUsersListUseCase: GetUsersListUseCase
) : BaseViewModel() {

    private val _usersList by lazy { MutableLiveData<MutableList<UserVO>>() }
    val usersList: MutableLiveData<MutableList<UserVO>> = _usersList

    private val _favList by lazy { MutableLiveData<MutableList<UserVO>>() }
    val favList: MutableLiveData<MutableList<UserVO>> = _favList

    init {
        loadUsers()
    }

    private fun loadUsers() {
        getUsersListUseCase.invoke(
            scope = viewModelScope,
            params = GetUsersListUseCase.Params(40)
        ) { result ->
            when (result) {
                is Result.Success -> {
                    _usersList.value = result.data.users.map().toMutableList()
                    loadFavUsers()
                }
                is Result.Failure -> {
                    error.value = result.error?.errorInfo?.message
                }
            }
        }
    }

    /** handle when user press on fav star to add/remove a user **/
    fun handleFavEvent(userVO: UserVO) {
        _usersList.value?.firstOrNull { it.name == userVO.name }?.let {
            it.isFav = true
            addToFav(it)
        } ?: run {
            _favList.value?.firstOrNull { it.name == userVO.name }?.let {
                it.isFav = false
                removeFromFav(it)
            }
        }
    }

    private fun loadFavUsers() {
        _favList.value = _usersList.value?.filter { it.isFav }?.toMutableList()
        _favList.value?.let { favList ->
            favList.forEach {
                    removeFromFav(it)
            }
        }
    }

    private fun addToFav(userVO: UserVO) {
        _favList.value?.add(0, userVO)
        _favList.value = _favList.value
        _usersList.value?.remove(userVO)
        _usersList.value = _usersList.value
    }

    private fun removeFromFav(userVO: UserVO) {
        _favList.value?.remove(userVO)
        _favList.value = _favList.value
        _usersList.value?.add(0, userVO)
        _usersList.value = _usersList.value
    }

}