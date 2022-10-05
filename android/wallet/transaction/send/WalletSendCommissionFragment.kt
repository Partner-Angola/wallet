package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.EthGasPrice
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendCommissionBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendCommissionFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendCommissionBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()
    private var ethStandard: EthGasPrice? = null
    private var ethFast: EthGasPrice? = null
    private var ethFastest: EthGasPrice? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendCommissionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        parentViewModel.ethGasPrice.observe(this) { items ->
            binding.btnStandardTrans.visibility = View.GONE
            binding.btnFastTrans.visibility = View.GONE
            binding.btnFastestTrans.visibility = View.GONE
            items.forEach { gasPrice ->
                when (gasPrice.type) {
                    "standard" -> {
                        ethStandard = gasPrice
                        binding.tvStandardTransCommission.text = "${gasPrice.gas_gwei} GWEI"
                        binding.tvStandardTransTime.text = gasPrice.delay_time
                        binding.btnStandardTrans.visibility = View.VISIBLE
                    }
                    "fast" -> {
                        ethFast = gasPrice
                        binding.tvFastTransCommission.text = "${gasPrice.gas_gwei} GWEI"
                        binding.tvFastTransTime.text = gasPrice.delay_time
                        binding.btnFastTrans.visibility = View.VISIBLE
                    }
                    "fastest" -> {
                        ethFastest = gasPrice
                        binding.tvFastestTransCommission.text = "${gasPrice.gas_gwei} GWEI"
                        binding.tvFastestTransTime.text = gasPrice.delay_time
                        binding.btnFastestTrans.visibility = View.VISIBLE
                    }
                }
            }
            if (ethStandard != null) {
                binding.currentCommission = ethStandard!!.type
                parentViewModel.setCommission(ethStandard!!.gas_gwei.toDouble())
            } else if (ethFast != null) {
                binding.currentCommission = ethFast!!.type
                parentViewModel.setCommission(ethFast!!.gas_gwei.toDouble())
            } else if (ethFastest != null) {
                binding.currentCommission = ethFastest!!.type
                parentViewModel.setCommission(ethFastest!!.gas_gwei.toDouble())
            }
        }
    }

    override fun init() {
        when (parentViewModel.getTokenType()) {
            WalletTokenType.ETH -> {
                binding.tvTopTitle.text = getString(R.string.wallet_send_commission_top_title)
                binding.lyEthCommission.visibility = View.VISIBLE
                binding.lySolCommission.visibility = View.GONE
                parentViewModel.getCommissionList()
            }
            WalletTokenType.SOL -> {
                binding.tvTopTitle.text = getString(R.string.wallet_send_commission_top_title2)
                binding.lyEthCommission.visibility = View.GONE
                binding.lySolCommission.visibility = View.VISIBLE
                binding.tvCommissionAmount.text = "0.000005 SOL"
                parentViewModel.setCommission(0.000005)
            }
        }
        binding.tvReceiveAddress.text = parentViewModel.getReceiveAddress()
        binding.tvSendAmount.text = parentViewModel.getSendAmount().toString()
        binding.btnBack.setOnClickListener { parentViewModel.prevPage() }
        binding.btnStandardTrans.setOnClickListener {
            if (ethStandard != null) {
                binding.currentCommission = ethStandard!!.type
                parentViewModel.setCommission(ethStandard!!.gas_gwei.toDouble())
            }
        }
        binding.btnFastTrans.setOnClickListener {
            if (ethFast != null) {
                binding.currentCommission = ethFast!!.type
                parentViewModel.setCommission(ethFast!!.gas_gwei.toDouble())
            }
        }
        binding.btnFastestTrans.setOnClickListener {
            if (ethFastest != null) {
                binding.currentCommission = ethFastest!!.type
                parentViewModel.setCommission(ethFastest!!.gas_gwei.toDouble())
            }
        }
        binding.btnNext.setOnClickListener {
            when (parentViewModel.getTokenType()) {
                WalletTokenType.ETH -> {
                    if (binding.currentCommission != null) {
                        parentViewModel.nextPage()
                    }
                }
                WalletTokenType.SOL -> {
                    parentViewModel.nextPage()
                }
            }
        }
    }
}