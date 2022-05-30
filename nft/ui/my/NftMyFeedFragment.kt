package com.joeware.android.gpulumera.nft.ui.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentNftMyFeedBinding

class NftMyFeedFragment : BaseFragment() {

    private lateinit var binding: FragmentNftMyFeedBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftMyFeedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {

    }
}