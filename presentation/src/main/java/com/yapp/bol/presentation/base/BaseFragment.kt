package com.yapp.bol.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.yapp.bol.presentation.firebase.Tracer

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes private val layoutRes: Int) : Fragment() {

    private var _binding: T? = null
    val binding: T
        get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this@BaseFragment
        onViewCreatedAction()
    }

    override fun onResume() {
        super.onResume()
        sendScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected open fun onViewCreatedAction() {}

    private fun sendScreen() {
        Tracer.screen(requireActivity(), getScreenName())
    }

    abstract fun getScreenName(): String
}
