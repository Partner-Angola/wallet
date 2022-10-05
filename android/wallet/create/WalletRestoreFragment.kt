package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletRestoreBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletRestoreFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletRestoreBinding
    private val viewModel: WalletRestoreViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletRestoreBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.walletInfo.observe(this) { walletInfo ->
            parentViewModel.setWalletInfo(walletInfo)
            parentViewModel.successCreateWallet()
        }
    }

    override fun init() {
        binding.btnNext.setOnClickListener {
            if (binding.etSeed.text.toString().isNullOrEmpty()) {
                Toast.makeText(context, getString(R.string.seed_restore_empty), Toast.LENGTH_SHORT).show()
            } else {
                parentViewModel.getPassword()?.let { password ->
                    viewModel.getSeed(password, binding.etSeed.text.toString())
                } ?: run { parentViewModel.cancelFinish() }
            }
        }
    }
}