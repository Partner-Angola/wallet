package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendFinishBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendFinishFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendFinishBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        parentViewModel.getSelectToken()?.let { token ->
            binding.tvTitle.text = "${token.name} ${getString(R.string.wallet_send_finish_success)}"
            binding.tvSendAmount.text = "${parentViewModel.getSendAmount()} ${token.symbol}"
            binding.tvSendAddress.text = parentViewModel.getMyAddress() ?: ""
            binding.tvCommissionAmount.text = parentViewModel.getCommissionStr()
            Glide.with(this).load(token.iconRes).into(binding.ivSymbolIcon)
        }
        binding.btnNext.setOnClickListener { parentViewModel.nextPage() }
    }
}