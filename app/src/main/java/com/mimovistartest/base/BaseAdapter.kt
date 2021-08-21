package com.mimovistartest.base

import androidx.recyclerview.widget.RecyclerView
import com.mimovistartest.util.OnAddDataSetListener

abstract class BaseAdapter<T>:  RecyclerView.Adapter<BaseViewHolder<T>>(),
    OnAddDataSetListener<T> {

    private val dataSet = mutableListOf<T>()

    override fun addDataSet(items: List<T>) {
        dataSet.clear()
        if (items.isNotEmpty())
            dataSet.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
        holder.bind(dataSet[position])

    override fun getItemCount(): Int = dataSet.size

}