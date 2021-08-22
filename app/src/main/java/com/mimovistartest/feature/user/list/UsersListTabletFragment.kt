package com.mimovistartest.feature.user.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.util.PaginationListener
import com.mimovistartest.util.StartSnapHelper

class UsersListTabletFragment :
    BaseFragment<FragmentUserListBinding, UsersListTabletViewModel>(UsersListTabletViewModel::class) {
    override fun getLayoutId(): Int = R.layout.fragment_user_list


    override fun init() {
        super.init()
        setUpView()
    }

    private fun setUpView() {
        //Set up the recycler view
        binding.userListTablet?.let { userRV ->
            (userRV.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListTabletAdapter(
                userClickListener = { userVO ->
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    viewModel.handleFavEvent(userVO)
                }
            )
            adapter.setHasStableIds(true)
            userRV.adapter = adapter

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            userRV.onFlingListener = null
            snapHelper.attachToRecyclerView(userRV)
        }

        binding.favUserListTablet?.let { favUserRV ->
            (favUserRV.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListTabletAdapter(
                userClickListener = { userVO ->
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    viewModel.handleFavEvent(userVO)
                }
            )
            adapter.setHasStableIds(true)
            favUserRV.adapter = adapter

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            favUserRV.onFlingListener = null
            snapHelper.attachToRecyclerView(favUserRV)
        }
    }

    override fun onScreenRotated(savedInstanceState: Bundle) {
        super.onScreenRotated(savedInstanceState)
        setUpView()
    }
}