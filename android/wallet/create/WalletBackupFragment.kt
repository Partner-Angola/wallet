package com.joeware.android.gpulumera.account.wallet.create

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletBackupBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletBackupFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletBackupBinding
    private val viewModel: WalletBackupViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletBackupBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.walletInfo.observe(this) { walletInfo -> parentViewModel.setWalletInfo(walletInfo) }
        viewModel.seed.observe(this) {binding.seed = it}
    }

    override fun init() {
        parentViewModel.getPassword()?.let { password -> viewModel.getSeed(password) } ?: run { parentViewModel.cancelFinish() }
        binding.btnCheck.setOnClickListener { binding.isAgree = !(binding.isAgree ?: false) }
        binding.btnSeedCopy.setOnClickListener {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                clipboard.setPrimaryClip(ClipData.newPlainText("label", binding.tvSeed.text))
                Toast.makeText(context, getString(R.string.wallet_seed_copy), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnNext.setOnClickListener { if (binding.isAgree == true) parentViewModel.successCreateWallet() }
    }
}