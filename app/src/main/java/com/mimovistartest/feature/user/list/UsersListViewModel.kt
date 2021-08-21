package com.mimovistartest.feature.user.list

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mimovistartest.base.BaseViewModel
import com.mimovistartest.data.api.RandomCoApiException
import com.mimovistartest.domain.usecases.GetUsersListUseCase
import com.mimovistartest.data.util.Result
import com.mimovistartest.domain.usecases.AddUserDBUseCase
import com.mimovistartest.domain.usecases.RemoveUserDBUseCase
import com.mimovistartest.model.UserVO
import com.mimovistartest.model.joinList
import com.mimovistartest.model.map

class UsersListViewModel(
    private val getUsersListUseCase: GetUsersListUseCase,
    private val addUserDBUseCase: AddUserDBUseCase,
    private val removeUserDBUseCase: RemoveUserDBUseCase
): BaseViewModel() {

    /** loadingIndicator display the loading state in screen **/
    private val _loadingIndicator by lazy { MutableLiveData<Boolean>() }
    val loadingIndicator = _loadingIndicator

    /** loadingIndicator display the loading state in screen **/
    private val _scrollCompleted by lazy { MutableLiveData<Boolean>() }
    val scrollCompleted = _scrollCompleted

    private val _usersList by lazy { MutableLiveData<List<UserVO>>() }

    private val _updatedList = MutableLiveData<MutableList<UserVO>>()

    private val _totalList = MutableLiveData<MutableList<UserVO>>()

    private var sortBy: SortBy = SortBy.NONE

    private var isFilterEnabled : Boolean = false

    init {
        loadUsers()
    }

    val list = MediatorLiveData<MutableList<UserVO>>().apply {
        addSource(_usersList) {
            _totalList.value = _totalList.value.joinList(it)
            when (sortBy) {
                SortBy.NAME -> sortBy(SortBy.NAME, _totalList.value?.toMutableList())
                SortBy.GENDER -> sortBy(SortBy.GENDER, _totalList.value?.toMutableList())
                else -> {
                    if(_totalList.value?.isEmpty() == true) error.value = RandomCoApiException.EMPTY_RESULT
                    value = _totalList.value?.toMutableList()
                }
            }
        }
        addSource(_updatedList) {
            if(it.isEmpty()) error.value = RandomCoApiException.EMPTY_RESULT
            value = it
        }
    }

    fun loadUsers() {
        loading(true)
        getUsersListUseCase.invoke(
            scope = viewModelScope,
            params = GetUsersListUseCase.Params(40)
        ){ result ->
            when (result) {
                is Result.Success -> {
                    _usersList.value = result.data.users.map()
                }
                is Result.Failure -> {
                    loading(false)
                    error.value = ""
                }
            }
        }
    }

    fun loading(value: Boolean) {
        _loadingIndicator.value = value
        if(value) _scrollCompleted.value = false
    }

    fun isLoading(): Boolean = _loadingIndicator.value ?: false

    fun scrollCompleted() {
        _scrollCompleted.value = true
    }

    /** update list which is visible to the user **/
    private fun updateList(newList: MutableList<UserVO>?){
        _updatedList.value = newList
    }

    /** handle when user press on fav star to add/remove a user **/
    fun handleFavEvent(userVO: UserVO) {
        list.value?.firstOrNull { it.name == userVO.name  }?.isFav = !userVO.isFav
        updateList(list.value)
        updateFavDB(userVO)
    }

    /** update fav user in local database **/
    private fun updateFavDB(userVO: UserVO) {
        if (userVO.isFav)
            insertUserToDB(userVO, isFav = 1)
        else
            removeUserDBUseCase.invoke(
                scope = viewModelScope,
                params = RemoveUserDBUseCase.Params(userVO.email)
            )
    }

    private fun insertUserToDB(userVO: UserVO, isFav: Int = 0, isRemoved: Int = 0) {
        addUserDBUseCase.invoke(
            scope = viewModelScope,
            params = AddUserDBUseCase.Params(userVO.email, isFav, isRemoved)
        )
    }

    fun deleteUser(userVO: UserVO) {
        insertUserToDB(userVO, isRemoved = 1)
        _totalList.value?.remove(userVO)
        updateList(_totalList.value?.toMutableList())
    }

    fun sortBy(type: SortBy, listToSort: MutableList<UserVO>? = list.value) {
        sortBy = type
        when(type) {
            SortBy.NAME -> {
                listToSort?.sortBy { user-> user.name }
                updateList(listToSort)
            }
            SortBy.GENDER -> {
                listToSort?.sortBy { user-> user.gender }
                updateList(listToSort)
            }
            SortBy.GENDER_NAME -> {
                updateList(listToSort?.sortedWith(compareBy({ it.gender }, { it.name }))?.toMutableList())
            }
            else -> updateList(
                if(isFilterEnabled())listToSort
                else _totalList.value?.toMutableList()
            )
        }
    }

    fun searchByNameOrEmail(typedText: String, listToSearch: MutableList<UserVO>? = _totalList.value) {
        if (typedText.isNotEmpty())
            updateList(listToSearch?.filter { user -> user.name.contains(typedText, ignoreCase = true) ||
                    user.email.contains(typedText, ignoreCase = true) }?.toMutableList())
        else
            updateList(_totalList.value?.toMutableList())

        isFilterEnabled = typedText.isNotEmpty()
    }

    fun isFilterEnabled(): Boolean = isFilterEnabled
}

enum class SortBy {
    GENDER,
    NAME,
    GENDER_NAME,
    NONE
}