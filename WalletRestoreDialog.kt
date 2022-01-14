package com.joeware.android.gpulumera.reward.ui.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletRestoreBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletRestoreDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletRestoreDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) = manager?.let {
            WalletRestoreDialog().show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletRestoreBinding
    private val viewModel: WalletMainViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletRestoreBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.isSuccessRestore.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                dismiss()
                Toast.makeText(context, "해당 Seed의 지갑이 확인되었습니다.", Toast.LENGTH_SHORT).show()
                viewModel.showWalletCreateAuthActivity()
            } else {
                Toast.makeText(context, "해당 Seed의 지갑을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun init() {
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnWalletRestore.setOnClickListener { viewModel.restoreWallet(binding.etSeed.text.toString()) }
    }
}