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
import com.mimovistartest.BR
import com.mimovistartest.util.OnShowErrorListener
import kotlin.reflect.KClass
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel>(viewModelClass: KClass<V>) :
    Fragment() {

    protected val viewModel: V by viewModel(viewModelClass)
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
    ): View? = inflater.inflate(getLayoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isScreenRotating = savedInstanceState?.getBoolean(IS_SCREEN_ROTATING) ?: false

        initDataBinding()
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


    private fun initDataBinding() {
        binding = DataBindingUtil.bind<T>(requireView())!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(BR.viewModel, this@BaseFragment.viewModel)
        lifecycle.addObserver(this@BaseFragment.viewModel)
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
