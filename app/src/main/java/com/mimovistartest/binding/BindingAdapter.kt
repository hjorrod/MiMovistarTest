package com.mimovistartest.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mimovistartest.util.OnAddDataSetListener
import com.mimovistartest.util.loadUrl
import org.koin.core.KoinComponent

object BindingAdapter : KoinComponent {

    /**
     * Set items into a recycler view. Each recycler view will implement [OnAddDataSetListener],
     * that will be called to update it own data set.
     */
    @BindingAdapter("items")
    @JvmStatic
    fun RecyclerView.setRecyclerViewDataSet(items: List<Any>?) {
        if (items != null && adapter != null && adapter is OnAddDataSetListener<*>){
            (adapter as OnAddDataSetListener<Any>).addDataSet(items)
        }
    }

    /**
     * Load a [imageUrl] into [imageView]. If [imageUrl] is null or empty, hide [imageView]
     */
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) imageView.visibility = View.GONE
        else {
            imageView.loadUrl(imageUrl)
            imageView.visibility = View.VISIBLE
        }
    }
}