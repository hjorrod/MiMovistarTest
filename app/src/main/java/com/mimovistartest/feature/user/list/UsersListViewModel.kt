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
import com.mimovistartest.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
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

    /**
     * _usersList: user list received from server in each request
     * _updatedList: user list ready to load in visible list (list) after change fav or remove user state
     * _totalList: save the original total list, which is updated incrementally after each request to server
     */
    private val _usersList by lazy { MutableLiveData<List<UserVO>>() }
    private val _updatedList = MutableLiveData<MutableList<UserVO>>()
    private val _totalList = MutableLiveData<MutableList<UserVO>>()

    private var sortType: SortType = SortType.NONE

    private var isFilterEnabled : Boolean = false

    init {
        loadUsers()
    }

    /** list is the visible list to the user */
    val list = MediatorLiveData<MutableList<UserVO>>().apply {
        addSource(_usersList) {
            _totalList.value = _totalList.value.joinList(it)
            when (sortType) {
                SortType.NAME -> sortBy(SortType.NAME, _totalList.value?.toMutableList())
                SortType.GENDER -> sortBy(SortType.GENDER, _totalList.value?.toMutableList())
                else -> {
                    _totalList.value?.let { list -> checkIfEmptyList(list) }
                    value = _totalList.value?.toMutableList()
                }
            }
        }
        addSource(_updatedList) {
            checkIfEmptyList(it)
            value = it
        }
    }

    /** fun to request random users to server */
    fun loadUsers() {
        loading(true)
        getUsersListUseCase.invoke(
            scope = viewModelScope,
            params = GetUsersListUseCase.Params(Constants.NUM_USER_REQUEST)
        ){ result ->
            when (result) {
                is Result.Success -> _usersList.value = result.data.users.map()
                is Result.Failure -> {
                    loading(false)
                    error.value = result.error?.errorInfo?.message
                    //check if visible list is empty
                    list.value?.let { checkIfEmptyList(it) }
                }
            }
        }
    }

    /** fun to handle the loading state in the view */
    fun loading(value: Boolean) {
        _loadingIndicator.value = value
        if(value) _scrollCompleted.value = false
    }

    /** fun to handle the loading state in the view
     * if scroll is completed, load_more button will be visible
     * */
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
            insertUserToDB(userVO, isFav = Constants.USER_AS_FAV)
        else
            removeUserDBUseCase.invoke(
                scope = viewModelScope,
                params = RemoveUserDBUseCase.Params(userVO.email)
            )
    }

    /** insert in local database the user who has been marked as fav or removed
     * if is marked as Fav and the user is received from the user again, it will be shown to the user as Fav
     * if is deleted and the user is received from the user again, it will not be shown to the user
     **/
    private fun insertUserToDB(userVO: UserVO, isFav: Int = 0, isRemoved: Int = 0) {
        addUserDBUseCase.invoke(
            scope = viewModelScope,
            params = AddUserDBUseCase.Params(userVO.email, isFav, isRemoved)
        )
    }

    /** delete user from the list and insert in local database to avoid be shown again to the user **/
    fun deleteUser(userVO: UserVO) {
        insertUserToDB(userVO, isRemoved = Constants.USER_DELETED)
        _totalList.value?.remove(userVO)
        updateList(_totalList.value?.toMutableList())
    }

    fun sortBy(type: SortType, listToSort: MutableList<UserVO>? = list.value) {
        sortType = type
        when(type) {
            SortType.NAME -> {
                listToSort?.sortBy { user-> user.name }
                updateList(listToSort)
            }
            SortType.GENDER -> {
                listToSort?.sortBy { user-> user.gender }
                updateList(listToSort)
            }
            SortType.GENDER_NAME -> {
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

    private fun checkIfEmptyList(list: MutableList<UserVO>) {
        if(list.isEmpty()) {
            loading(false)
            error.value = RandomCoApiException.EMPTY_RESULT
            //set _scrollCompleted to true to make visible load_more button
            _scrollCompleted.value = true
        }

    }

    fun isLoading(): Boolean = _loadingIndicator.value ?: false

    fun isFilterEnabled(): Boolean = isFilterEnabled
}

enum class SortType {
    GENDER,
    NAME,
    GENDER_NAME,
    NONE
}