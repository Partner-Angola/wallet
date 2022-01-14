package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendAddressBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendAddressFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletSendAddressBinding
    private val viewModel: WalletSendViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendAddressBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }


    override fun init() {
        viewModel.setSendAddress.observe(this, Observer { binding.etAddress.setText(it) })
        binding.btnNext.setOnClickListener {
            viewModel.finishSetAddress(binding.etAddress.text.toString())
        }
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}