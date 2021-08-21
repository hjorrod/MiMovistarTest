package com.mimovistartest.feature.user.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.model.UserVO
import com.mimovistartest.util.*
import org.koin.android.ext.android.bind


class UsersListFragment :
    BaseFragment<FragmentUserListBinding, UsersListViewModel>(UsersListViewModel::class) {
    override fun getLayoutId(): Int = R.layout.fragment_user_list

    override fun addBindingVariables() {
        super.addBindingVariables()
        with(binding) {
            setLoadMoreClicked(this@UsersListFragment::onLoadMoreClicked)
        }
    }

    override fun init() {
        super.init()
        setUpView()
    }

    private fun setUpView() {
        //Set up the recycler view
        binding.userList.apply {
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListAdapter(
                loadListener = {
                    viewModel.loading(false)
                },
                userClickListener = { userVO ->
                    Log.d("randomCo", " user Clicked")
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    Log.d("randomCo", " onFav Clicked")
                    viewModel.handleFavEvent(userVO)
                },
                removeClickListener = {
                    Log.d("randomCo", " remove Clicked")
                    viewModel.deleteUser(it)
                }
            )
            this.adapter = adapter

            /** add listener to handle when show the Load_More button to load more users **/
            addOnScrollListener(object :
                PaginationListener(layoutManager as LinearLayoutManager) {
                override fun isFilterEnabled(): Boolean = viewModel.isFilterEnabled() ?: false
                override fun isLoading(): Boolean = viewModel.isLoading() ?: false
                override fun scrollCompleted() {
                    viewModel.scrollCompleted()
                }
            })

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            onFlingListener = null
            snapHelper.attachToRecyclerView(this)
        }

        binding.apply {
            /** set Listener to edit text to filter by name **/
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d("randomCo", " onTextChanged $s - $count")
                    handlerSearch.removeCallbacks(runnableSearch)
                    handlerSearch.postDelayed(runnableSearch, 1200)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /** set Listener to checkBox to Sort the list by Name or by Gender **/
            cbSortByName.setOnCheckedChangeListener { _, isChecked ->
                Log.d("randomCo", " SortBy.NAME Clicked $isChecked")
                sortUsersList(isChecked, cbSortByGender.isChecked)
            }
            cbSortByGender.setOnCheckedChangeListener { _, isChecked ->
                Log.d("randomCo", " SortBy.GENDER Clicked $isChecked")
                sortUsersList(cbSortByName.isChecked, isChecked)
            }
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers()
    }

    private fun sortUsersList(byName: Boolean, byGender: Boolean) {
        viewModel.sortBy(when {
            byName && byGender -> SortBy.GENDER_NAME
            byGender -> SortBy.GENDER
            byName -> SortBy.NAME
            else -> SortBy.NONE
        })
    }

    /** on reload button clicked **/
    private fun onLoadMoreClicked(view: View) {
        loadUsers()
    }

    override fun onScreenRotated(savedInstanceState: Bundle) {
        super.onScreenRotated(savedInstanceState)
        setUpView()
    }

    private var handlerSearch = Handler(Looper.getMainLooper())
    private val runnableSearch = Runnable {
        viewModel.searchByName(binding.etSearch.text.toString())
    }
}