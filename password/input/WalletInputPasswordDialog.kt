package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletInputPasswordBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletInputPasswordDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletInputPasswordDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, func: () -> Unit) = manager?.let {
            WalletInputPasswordDialog().apply { successFunc = func }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletInputPasswordBinding
    private val viewModel: WalletInputPasswordViewModel by viewModel()
    private var successFunc: (() -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletInputPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.successCheckPassword.observe(this, Observer { successFunc?.let { it() } })
        viewModel.dismissDialog.observe(this, Observer { dismiss() })
    }

    override fun init() {
        binding.btnNext.setOnClickListener {
            viewModel.checkPassword(binding.etPassword.text.toString())
        }

        val content = SpannableString(binding.btnForgetPassword.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.btnForgetPassword.text = content

    }


}