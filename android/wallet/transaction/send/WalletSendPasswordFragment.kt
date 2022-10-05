package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendPasswordBinding
import com.joeware.android.gpulumera.util.AccountUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletSendPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendPasswordBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()
    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        showIncorrectMessage(false)
        binding.btnBack.setOnClickListener { parentViewModel.prevPage() }
        binding.btnNext.setOnClickListener {
            val inputPassword = binding.etPassword.text.toString()
            if (inputPassword.isEmpty()) {
                showIncorrectMessage(true, getString(R.string.wallet_send_password_input))
            } else if (accountUtil.getPassword() == inputPassword) {
                parentViewModel.successInputPassword()
            } else {
                showIncorrectMessage(true, getString(R.string.wallet_send_password_incorrect))
            }
        }
    }

    private fun showIncorrectMessage(show: Boolean, message: String = "") {
        binding.showMessage = show
        binding.message = message
    }
}