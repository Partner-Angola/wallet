package com.joeware.android.gpulumera.reward.ui.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletCreateGuidBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletCreateGuidDialog  : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletCreateGuidDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) = manager?.let {
            WalletCreateGuidDialog().show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletCreateGuidBinding
    private val viewModel: WalletMainViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletCreateGuidBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnWalletCreate.setOnClickListener { viewModel.showWalletCreateAuthActivity();dismiss() }
        binding.btnWalletRestore.setOnClickListener { viewModel.showWalletRestoreDialog();dismiss() }
    }

}