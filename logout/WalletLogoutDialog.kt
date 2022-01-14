package com.joeware.android.gpulumera.reward.ui.wallet.logout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletLogoutBinding

class WalletLogoutDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletLogoutDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, func: () -> Unit) = manager?.let {
            WalletLogoutDialog().apply { successFunc = func }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletLogoutBinding
    private var successFunc: (() -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletLogoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnLogout.setOnClickListener {
            successFunc?.let { it() }
            dismiss()
        }
    }
}