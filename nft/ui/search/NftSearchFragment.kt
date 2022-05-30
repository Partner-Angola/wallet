package com.joeware.android.gpulumera.nft.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentNftSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftSearchFragment : BaseFragment() {

    private lateinit var binding: FragmentNftSearchBinding
    private val viewModel: NftSearchViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {

    }
}