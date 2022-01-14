package com.joeware.android.gpulumera.reward.ui.wallet.receive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletReceiveBinding
import com.joeware.android.gpulumera.util.safeLet
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletReceiveDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletReceiveDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, mnemonic: String, pubKey: String) = manager?.let {
            val bundle = Bundle().apply {
                putString("mnemonic", mnemonic)
                putString("pubKey", pubKey)
            }
            WalletReceiveDialog().apply { arguments = bundle }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletReceiveBinding
    private val viewModel: WalletReceiveViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletReceiveBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.isAgree = false
        return binding.root
    }

    override fun setObserveData() {
        viewModel.createQrBitmap.observe(this, Observer {
            binding.ivQr.setImageBitmap(it)
        })
        viewModel.copyPubKey.observe(this, Observer { pubKey ->
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                clipboard.setPrimaryClip(ClipData.newPlainText("label", pubKey))
                Toast.makeText(context, "Seed가 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.clickBack.observe(this, Observer { dismiss() })
    }

    override fun init() {
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        safeLet(arguments?.getString("mnemonic"), arguments?.getString("pubKey")) { mnemonic, pubKey ->
            viewModel.createQRCode(mnemonic, pubKey)
        }
    }

}
