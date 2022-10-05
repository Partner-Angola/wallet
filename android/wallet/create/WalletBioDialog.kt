package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletBioBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletBioDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletBioDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, finishEvent: () -> Unit) = manager?.let {
            WalletBioDialog().apply { setFinishEvent(finishEvent) }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletBioBinding
    private val viewModel: WalletBioViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()
    private var finishEvent: (() -> Unit)? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletBioBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.toastMessage.observe(this) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.successFinish.observe(this) { successFinish() }
        viewModel.cancelFinish.observe(this) { cancelFinish() }
    }

    override fun init() {
        viewModel.checkBio(requireActivity())
        binding.btnAfter.setOnClickListener { cancelFinish() }
        binding.btnClose.setOnClickListener { cancelFinish() }
        binding.btnBackground.setOnClickListener { cancelFinish() }
        binding.btnUse.setOnClickListener { viewModel.useBio() }
    }

    private fun successFinish() {
        parentViewModel.setUseBio(true)
        finishEvent?.let { it() }
        dismiss()
    }

    private fun cancelFinish() {
        parentViewModel.setUseBio(false)
        finishEvent?.let { it() }
        dismiss()
    }

    fun setFinishEvent(event: () -> Unit) {
        this.finishEvent = event
    }

}