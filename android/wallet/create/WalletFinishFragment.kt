package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletFinishMode
import com.joeware.android.gpulumera.account.wallet.model.WalletPinMode
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletFinishBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletFinishFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletFinishBinding
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()
    private var finishMode: WalletFinishMode? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        finishMode = when(arguments?.getString("mode")) {
            WalletFinishMode.CREATE_MODE.name -> WalletFinishMode.CREATE_MODE
            WalletFinishMode.RESTORE_MODE.name -> WalletFinishMode.RESTORE_MODE
            else -> null
        }
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        finishMode?.let { mode ->
            when(mode) {
                WalletFinishMode.CREATE_MODE -> {
                    binding.tvTitle.text = getString(R.string.wallet_create_finish_create_title)
                }
                WalletFinishMode.RESTORE_MODE -> {
                    binding.tvTitle.text = getString(R.string.wallet_create_finish_restore_title)
                }
            }
        } ?: run {
            binding.tvTitle.text = getString(R.string.wallet_create_finish_create_title)
        }
        binding.btnNext.setOnClickListener { parentViewModel.nextPage() }
    }
}