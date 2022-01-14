package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletInputPinBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletInputPinDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletInputPinDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, func: () -> Unit) = manager?.let {
            WalletInputPinDialog().apply { successFunc = func }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletInputPinBinding
    private val viewModel: WalletInputPinViewModel by viewModel()
    private var successFunc: (() -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletInputPinBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.successAuth.observe(this, Observer {
            successFunc?.let { it() }
            dismiss()
        })
        viewModel.failAuth.observe(this, Observer {  })
        viewModel.failAuthBio.observe(this, Observer {  })
    }

    override fun init() {
        viewModel.shufflePinNumber()
        context?.let { viewModel.checkBio(it, this) }
    }


}