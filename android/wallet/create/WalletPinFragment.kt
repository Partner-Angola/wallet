package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.account.dialog.PinViewModel
import com.joeware.android.gpulumera.account.wallet.model.WalletPinMode
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletPinBinding
import com.joeware.android.gpulumera.util.PrefUtil
import com.jpbrothers.base.util.log.JPLog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent

class WalletPinFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletPinBinding
    private val viewModel: WalletPinViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()
    private val dialogViewModel: PinViewModel by sharedViewModel()
    private var pinMode: WalletPinMode? = null

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletPinBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        pinMode = when(arguments?.getString("mode")) {
            WalletPinMode.CREATE_MODE.name -> WalletPinMode.CREATE_MODE
            WalletPinMode.CREATE_CHECK_MODE.name -> WalletPinMode.CREATE_CHECK_MODE
            WalletPinMode.CHECK_INPUT_MODE.name -> WalletPinMode.CHECK_INPUT_MODE
            WalletPinMode.RESTORE_INPUT_MODE.name -> WalletPinMode.RESTORE_INPUT_MODE
            WalletPinMode.RESTORE_CREATE_MODE.name -> WalletPinMode.RESTORE_CREATE_MODE
            WalletPinMode.RESTORE_CREATE_CHECK_MODE.name -> WalletPinMode.RESTORE_CREATE_CHECK_MODE
            else -> null
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.finishInputPin.observe(this) { pin ->
            pinMode?.let { mode ->
                when(mode) {
                    WalletPinMode.CREATE_MODE, WalletPinMode.CREATE_CHECK_MODE -> parentViewModel.setPin(pin)
                    else -> dialogViewModel.setPin(pin)
                }
            }
        }
    }

    override fun init() {
        binding.btnBack.setOnClickListener {
            pinMode?.let { mode ->
                when (mode) {
                    WalletPinMode.CREATE_MODE, WalletPinMode.CREATE_CHECK_MODE -> parentViewModel.prevPage()
                    else -> dialogViewModel.prevPage()
                }
            }
        }
        pinMode?.let { mode ->
            when(mode) {
                WalletPinMode.CREATE_MODE -> viewModel.initWalletPin(requireContext(), mode, null)
                WalletPinMode.CREATE_CHECK_MODE -> viewModel.initWalletPin(requireContext(), mode, parentViewModel.getPin())
                WalletPinMode.CHECK_INPUT_MODE -> viewModel.initWalletPin(requireContext(), mode, prefUtil.userWalletPin)
                WalletPinMode.RESTORE_INPUT_MODE -> viewModel.initWalletPin(requireContext(), mode, prefUtil.userWalletPin)
                WalletPinMode.RESTORE_CREATE_MODE -> viewModel.initWalletPin(requireContext(), mode, null)
                WalletPinMode.RESTORE_CREATE_CHECK_MODE -> viewModel.initWalletPin(requireContext(), mode, dialogViewModel.getPin())
            }
        } ?: run {
            parentViewModel.cancelFinish()
        }
    }
}