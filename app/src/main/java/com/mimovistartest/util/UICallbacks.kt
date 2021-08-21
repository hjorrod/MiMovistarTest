package com.mimovistartest.util

import com.mimovistartest.model.UserVO

fun interface OnLoadedListener {
    fun loadFinished()
}

fun interface OnRemoveClickListener {
    fun onRemoveClickListener(userVO: UserVO)
}

fun interface OnFavStarClickListener {
    fun onFavStarClick(userVO: UserVO)
}

fun interface OnUserClickListener {
    fun onUserClick(userVO: UserVO)
}

fun interface OnAddDataSetListener<T> {
    fun addDataSet(items: List<T>)
}

fun interface OnShowErrorListener {
    fun showError(message: String)
}

