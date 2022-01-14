package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendSuccessBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendSuccessFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletSendSuccessBinding
    private val viewModel: WalletSendViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendSuccessBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }


    override fun init() {

    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}