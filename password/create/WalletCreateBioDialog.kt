package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletCreateBioBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletCreateBioDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletCreateBioDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) = manager?.let {
            WalletCreateBioDialog().show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletCreateBioBinding
    private val viewModel: WalletCreateAuthViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletCreateBioBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.successCheckBio.observe(this, Observer { dismiss() })
    }

    override fun init() {
        binding.btnAfter.setOnClickListener { viewModel.unUseBio();dismiss() }
        binding.btnClose.setOnClickListener { viewModel.unUseBio();dismiss() }
        binding.btnBackground.setOnClickListener { viewModel.unUseBio();dismiss() }
        binding.btnUse.setOnClickListener { viewModel.useBio() }
    }
}