package com.mimovistartest.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mimovistartest.util.OnShowErrorListener
import kotlin.reflect.KClass

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel>(val viewModelClass: KClass<V>) :
    Fragment() {

    protected open val viewModel: BaseViewModel by viewModels()

    protected lateinit var binding: T
    private var showErrorListener: OnShowErrorListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnShowErrorListener) showErrorListener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isScreenRotating = savedInstanceState?.getBoolean(IS_SCREEN_ROTATING) ?: false

        setUpDataBinding()
        getBundleArguments()
        observeBase()
        observeViewModel()

        if (!isScreenRotating)
            init()
        else
            savedInstanceState?.let { onScreenRotated(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_SCREEN_ROTATING, requireActivity().isChangingConfigurations)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun init() {}
    open fun observeViewModel() {}
    open fun getBundleArguments() {}
    open fun addBindingVariables() {}
    open fun onScreenRotated(savedInstanceState: Bundle) { }


    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        addBindingVariables()
    }

    private fun observeBase() {
        viewModel.showErrorEvent.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { showError(it) }
        })
    }

    private fun showError(message: String) {
        showErrorListener?.showError(message)
    }

    companion object {
        private const val IS_SCREEN_ROTATING = "IS_SCREEN_ROTATING"
    }
}
