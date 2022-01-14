package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendAmountBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendAmountFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletSendAmountBinding
    private val viewModel: WalletSendViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendAmountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveLiveData() {
        viewModel.setMaxAmount.observe(this, Observer { binding.etAmount.setText(it) })
    }

    override fun init() {
        binding.btnNext.setOnClickListener {
            viewModel.finishSetAmount(binding.etAmount.text.toString())
        }
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}