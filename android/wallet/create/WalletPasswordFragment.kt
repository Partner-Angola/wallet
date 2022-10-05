package com.joeware.android.gpulumera.account.wallet.create

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.dialog.PasswordViewModel
import com.joeware.android.gpulumera.account.wallet.model.WalletPasswordMode
import com.joeware.android.gpulumera.account.wallet.model.WalletPinMode
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletPasswordBinding
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent

class WalletPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletPasswordBinding
    private val viewModel: WalletPasswordViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()
    private val dialogViewModel: PasswordViewModel by sharedViewModel()
    private var passwordMode: WalletPasswordMode? = null

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        passwordMode = when(arguments?.getString("mode")) {
            WalletPasswordMode.CREATE_MODE.name -> WalletPasswordMode.CREATE_MODE
            WalletPasswordMode.CREATE_CHECK_MODE.name -> WalletPasswordMode.CREATE_CHECK_MODE
            WalletPasswordMode.CHECK_INPUT_MODE.name -> WalletPasswordMode.CHECK_INPUT_MODE
            WalletPasswordMode.RESTORE_INPUT_MODE.name -> WalletPasswordMode.RESTORE_INPUT_MODE
            WalletPasswordMode.RESTORE_CREATE_MODE.name -> WalletPasswordMode.RESTORE_CREATE_MODE
            WalletPasswordMode.RESTORE_CREATE_CHECK_MODE.name -> WalletPasswordMode.RESTORE_CREATE_CHECK_MODE
            else -> null
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.finishInputPassword.observe(this) { password ->
            when (passwordMode) {
                WalletPasswordMode.CREATE_CHECK_MODE -> WalletBioDialog.showDialog(childFragmentManager) { parentViewModel.setPassword(password) }
                WalletPasswordMode.CREATE_MODE -> parentViewModel.setPassword(password)
                else -> dialogViewModel.setPassword(password)
            }
        }
    }

    override fun init() {
        binding.btnBack.setOnClickListener {
            passwordMode?.let { mode ->
                when (mode) {
                    WalletPasswordMode.CREATE_MODE, WalletPasswordMode.CREATE_CHECK_MODE -> parentViewModel.prevPage()
                    else -> dialogViewModel.prevPage()
                }
            }
        }
        binding.btnNext.setOnClickListener {
            viewModel.inputPassword(requireContext(), binding.etPassword.text.toString())
        }
        binding.ivShowPassword.setOnClickListener {
            if (binding.etPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                binding.ivShowPassword.setImageResource(R.drawable.nft_btn_eye_on)
            } else if (binding.etPassword.inputType == InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivShowPassword.setImageResource(R.drawable.nft_btn_eye_off)
            }
            binding.etPassword.setSelection(binding.etPassword.length())
        }
        passwordMode?.let { mode ->
            when(mode) {
                WalletPasswordMode.CREATE_MODE -> viewModel.initWalletPassword(requireContext(), mode, null)
                WalletPasswordMode.CREATE_CHECK_MODE -> viewModel.initWalletPassword(requireContext(), mode, parentViewModel.getPassword())
                WalletPasswordMode.CHECK_INPUT_MODE -> viewModel.initWalletPassword(requireContext(), mode, prefUtil.userWalletPassword)
                WalletPasswordMode.RESTORE_INPUT_MODE -> viewModel.initWalletPassword(requireContext(), mode, prefUtil.userWalletPassword)
                WalletPasswordMode.RESTORE_CREATE_MODE -> viewModel.initWalletPassword(requireContext(), mode, null)
                WalletPasswordMode.RESTORE_CREATE_CHECK_MODE -> viewModel.initWalletPassword(requireContext(), mode, dialogViewModel.getPassword())
            }
        } ?: run {
            parentViewModel.cancelFinish()
        }
    }
}