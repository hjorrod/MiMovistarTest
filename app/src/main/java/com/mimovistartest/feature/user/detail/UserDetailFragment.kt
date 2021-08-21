package com.mimovistartest.feature.user.detail

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.navigation.fragment.navArgs
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserDetailBinding

class UserDetailFragment
    : BaseFragment<FragmentUserDetailBinding, UserDetailViewModel>(UserDetailViewModel::class) {

    private val args: UserDetailFragmentArgs by navArgs()

    override fun getLayoutId(): Int = R.layout.fragment_user_detail

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun getBundleArguments() {
        super.getBundleArguments()
        args.userVO.apply {
            binding.user = this
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}