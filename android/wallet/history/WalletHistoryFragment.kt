package com.joeware.android.gpulumera.account.wallet.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.account.wallet.model.EthHistory
import com.joeware.android.gpulumera.account.wallet.model.EthTokenHistory
import com.joeware.android.gpulumera.account.wallet.model.SolHistory
import com.joeware.android.gpulumera.account.wallet.model.WalletHistoryType
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletHistoryBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletHistoryFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletHistoryBinding
    private val viewModel: WalletHistoryViewModel by viewModel()
    private var historyType: WalletHistoryType? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletHistoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        historyType = when(arguments?.getString("type")) {
            WalletHistoryType.ETH_ANG.name -> {
                binding.adapter = WalletHistoryAdapter<EthTokenHistory>().apply { setHistoryType(WalletHistoryType.ETH_ANG) }
                WalletHistoryType.ETH_ANG
            }
            WalletHistoryType.ETH.name -> {
                binding.adapter = WalletHistoryAdapter<EthHistory>().apply { setHistoryType(WalletHistoryType.ETH) }
                WalletHistoryType.ETH
            }
            WalletHistoryType.SOL_ANG.name -> {
                binding.adapter = WalletHistoryAdapter<SolHistory>().apply { setHistoryType(WalletHistoryType.SOL_ANG) }
                WalletHistoryType.SOL_ANG
            }
            WalletHistoryType.SOL.name -> {
                binding.adapter = WalletHistoryAdapter<SolHistory>().apply { setHistoryType(WalletHistoryType.SOL) }
                WalletHistoryType.SOL
            }
            else -> null
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.ethItems.observe(this) { binding.adapter?.setItems(it) }
        viewModel.ethTokenItems.observe(this) { binding.adapter?.setItems(it) }
        viewModel.solItems.observe(this) { binding.adapter?.setItems(it) }
    }

    override fun init() {
        viewModel.getWalletHistory(historyType)
    }
}