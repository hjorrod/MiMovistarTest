package com.mimovistartest.feature.user.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mimovistartest.base.BaseAdapter
import com.mimovistartest.base.BaseViewHolder
import com.mimovistartest.databinding.UserItemBinding
import com.mimovistartest.model.UserVO
import com.mimovistartest.util.OnFavStarClickListener
import com.mimovistartest.util.OnUserClickListener

class UserListTabletAdapter(
    private val userClickListener: OnUserClickListener,
    private val favStarClickListener: OnFavStarClickListener
) : BaseAdapter<UserVO>() {

    override fun addDataSet(items: List<UserVO>) {
        super.addDataSet(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserVO> =
        UserViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemId(position: Int): Long = dataSet[position].id.toLong()

    inner class UserViewHolder(
        private val binding: UserItemBinding
    ) : BaseViewHolder<UserVO>(binding) {
        override fun bind(item: UserVO) {
            with(binding) {
                user = item
                onClickListener = userClickListener
                onFavStarListener = favStarClickListener
                //mark textView as selected to enable the text movement
                userName.isSelected = true
                userEmail.isSelected = true
            }
        }
    }
}