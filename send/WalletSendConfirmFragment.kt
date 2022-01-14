package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendConfirmBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendConfirmFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletSendConfirmBinding
    private val viewModel: WalletSendViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendConfirmBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }


    override fun init() {
        viewModel.showConfirmData()
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}