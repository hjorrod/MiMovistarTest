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
import com.mimovistartest.BR
import com.mimovistartest.R
import com.mimovistartest.util.OnShowErrorListener
import kotlin.reflect.KClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//@AndroidEntryPoint
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel>(val viewModelClass: KClass<V>) :
    Fragment() {

    //protected val viewModel: V by viewModel(viewModelClass)
    //@Inject
    //protected lateinit var viewModel: V
    //private val viewModel: V by viewModels<viewModelClass>()
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
    open fun observeViewModel() {
        //viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
    }
    open fun getBundleArguments() {}
    open fun addBindingVariables() {}
    open fun onScreenRotated(savedInstanceState: Bundle) { }


    private fun initDataBinding() {
        binding = DataBindingUtil.bind<T>(requireView())!!
        binding.lifecycleOwner = viewLifecycleOwner
        if (resources.getString(R.string.device_type) == "phone")
            binding.setVariable(BR.viewModel, this@BaseFragment.viewModel)
        else
            binding.setVariable(BR.viewModelTablet, this@BaseFragment.viewModel)
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
