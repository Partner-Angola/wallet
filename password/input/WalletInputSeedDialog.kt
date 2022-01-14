package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletInputSeedBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletInputSeedDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletInputSeedDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, func: (seed: String) -> Unit) = manager?.let {
            WalletInputSeedDialog().apply { successFunc = func }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletInputSeedBinding
    private val viewModel: WalletInputSeedViewModel by viewModel()
    private var successFunc: ((seed: String) -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletInputSeedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.dismissDialog.observe(this, Observer { dismiss() })
        viewModel.successCheckSeed.observe(this, Observer {
            successFunc?.let { it(binding.etSeed.text.toString()) }
            dismiss()
        })
    }

    override fun init() {
        binding.btnNext.setOnClickListener { viewModel.checkSeed(binding.etSeed.text.toString()) }
    }
}