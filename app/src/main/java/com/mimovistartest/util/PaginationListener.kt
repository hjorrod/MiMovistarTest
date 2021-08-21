package com.mimovistartest.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    abstract fun isFilterEnabled(): Boolean

    abstract fun isLoading(): Boolean

    abstract fun scrollCompleted()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if(!isLoading() && !isFilterEnabled()){
            val visibleItemCount = layoutManager.childCount
            val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            val total = layoutManager.itemCount
            if(visibleItemCount + firstVisibleItem >= total)
                scrollCompleted()
        }

    }
}