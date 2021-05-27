package com.example.moviecatalogue.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.core.util.DataState
import com.example.core.util.ImageUtil
import com.example.moviecatalogue.R
import com.example.moviecatalogue.databinding.IncludeMessageBinding
import com.example.moviecatalogue.databinding.IncludeProgressBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: Inflate<T>,
) : Fragment() {
    private var _binding: T? = null
    private var _progressBinding: IncludeProgressBinding? = null
    private var _messageBinding: IncludeMessageBinding? = null
    protected val binding get() = _binding as T
    private val progressBinding get() = _progressBinding as IncludeProgressBinding
    private val messageBinding get() = _messageBinding as IncludeMessageBinding
    protected lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = requireContext()
        super.onCreate(savedInstanceState)
    }

    open fun getRootViewGroup(): ViewGroup? = null

    open fun runOnCreateView() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        getRootViewGroup()?.let {
            _messageBinding = IncludeMessageBinding.inflate(inflater, getRootViewGroup(), true)
            _progressBinding = IncludeProgressBinding.inflate(inflater, getRootViewGroup(), true)
        }
        runOnCreateView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _messageBinding = null
        _progressBinding = null
    }

    protected fun showResult(
        type: DataState,
        subtitleMessage: String? = null,
    ) {
        if (getRootViewGroup() == null) return
        when (type) {
            DataState.LOAD -> {
                progressBinding.root.visibility = View.VISIBLE
                messageBinding.root.visibility = View.GONE
            }
            DataState.EXISTS -> {
                progressBinding.root.visibility = View.GONE
                messageBinding.root.visibility = View.GONE
            }
            DataState.NOT_FOUND, DataState.ERROR -> {
                progressBinding.root.visibility = View.GONE
                messageBinding.root.visibility = View.VISIBLE
                messageBinding.apply {
                    mainMessageDescriptionTv.apply {
                        text = subtitleMessage
                        isVisible = subtitleMessage !== null
                    }

                    val drawableId = when (type) {
                        DataState.NOT_FOUND -> R.drawable.ic_not_found
                        else -> R.drawable.ic_something_wrong
                    }
                    mainMessageImageIv.apply {
                        setImageDrawable(
                            ImageUtil.getDrawable(
                                mContext,
                                drawableId
                            )
                        )
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}