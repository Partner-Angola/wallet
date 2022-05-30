package com.joeware.android.gpulumera.nft.ui.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentNftCommunityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftCommunityFragment : BaseFragment() {

    private lateinit var binding: FragmentNftCommunityBinding
    private val viewModel: NftCommunityViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftCommunityBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {

    }
}