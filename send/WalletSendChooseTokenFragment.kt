package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendChooseTokenBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendChooseTokenFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletSendChooseTokenBinding
    private val viewModel: WalletSendViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendChooseTokenBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveLiveData() {

    }

    override fun init() {
        viewModel.getMyTokenAmount()
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}