package com.mimovistartest.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mimovistartest.R
import com.mimovistartest.data.util.InternetConnectionObserver
import com.mimovistartest.util.OnShowErrorListener
import com.mimovistartest.util.toast

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private val layout: Int) : AppCompatActivity(),
    OnShowErrorListener {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        observeInternetConnection()
        if (savedInstanceState?.getBoolean(IS_SCREEN_ROTATING) == true)
            onScreenRotated(savedInstanceState)
        else
            init()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_SCREEN_ROTATING, isChangingConfigurations)
        super.onSaveInstanceState(outState)
    }


    open fun init() {}
    open fun addBindingVariables() {}
    open fun onScreenRotated(savedInstanceState: Bundle) {}

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, layout)
        binding.lifecycleOwner = this
        addBindingVariables()
    }

    override fun showError(message: String) {
        if(message.isNotEmpty()) message.toast(this)
        else resources.getString(R.string.error_message).toast(this)
    }

    private fun observeInternetConnection() {
        InternetConnectionObserver.get().observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                showError(it.message)
                InternetConnectionObserver.get().removeObservers(this)
            }
        }
    }

    companion object {
        private const val IS_SCREEN_ROTATING = "IS_SCREEN_ROTATING"
    }
}
