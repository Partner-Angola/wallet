package com.joeware.android.gpulumera.nft.ui.market

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentNftMarketBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftMarketFragment : BaseFragment() {

    private lateinit var binding: FragmentNftMarketBinding
    private val viewModel: NftMarketViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftMarketBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {

    }
}