package com.joeware.android.gpulumera.account.wallet.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.account.wallet.model.WalletCreateMode
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletCreateBinding

class WalletCreateDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletCreateDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, successEvent: () -> Unit, failEvent: () -> Unit) = manager?.let {
            WalletCreateDialog().apply { setEvent(successEvent, failEvent) }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletCreateBinding
    private var successEvent: (() -> Unit)? = null
    private var failEvent:(() -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletCreateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCreate.setOnClickListener {
            walletCreateStartForResult.launch(WalletCreateActivity.startActivityIntent(requireActivity(), WalletCreateMode.CREATE_MODE))
        }
        binding.btnRestore.setOnClickListener {
            walletCreateStartForResult.launch(WalletCreateActivity.startActivityIntent(requireActivity(), WalletCreateMode.RESTORE_MODE))
        }
    }

    fun setEvent(successEvent: () -> Unit, failEvent: () -> Unit) {
        this.successEvent = successEvent
        this.failEvent = failEvent
    }

    private val walletCreateStartForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            successEvent?.let { it();dismiss() }
        } else if(result.resultCode == RESULT_CANCELED) {
            failEvent?.let { it();dismiss() }
        }
    }
}