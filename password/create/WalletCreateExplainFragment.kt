package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletCreateExplainBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletCreateExplainFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletCreateExplainBinding
    private val viewModel: WalletCreateAuthViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletCreateExplainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vpMain.adapter = WalletCreateExplainAdapter()
        binding.indicator.setViewPager2(binding.vpMain)
        binding.vm = viewModel
        return binding.root
    }

    override fun getLayoutRes(): Int = 0
    override fun findViews(root: View?) {}

    override fun setObserveLiveData() {

    }

    override fun init() {

    }

}