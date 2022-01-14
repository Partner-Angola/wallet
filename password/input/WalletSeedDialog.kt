package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletInputPinBinding
import com.joeware.android.gpulumera.databinding.DialogWalletSeedBinding
import com.joeware.android.gpulumera.databinding.FragmentWalletCreateSeedBinding
import com.joeware.android.gpulumera.reward.ui.home.RewardHomeActivity
import com.joeware.android.gpulumera.reward.ui.home.RewardHomeViewModel
import com.joeware.android.gpulumera.reward.ui.wallet.WalletMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletSeedDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletSeedDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) = manager?.let {
            WalletSeedDialog().show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletSeedBinding
    private val viewModel: WalletSeedViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletSeedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        viewModel.init()
        return binding.root
    }

    override fun setObserveData() {
        viewModel.clickClose.observe(this, Observer {
            dismiss()
        })
    }

    override fun init() {
        binding.btnSeedCopy.setOnClickListener {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                clipboard.setPrimaryClip(ClipData.newPlainText("label", binding.tvSeed.text))
                Toast.makeText(context, "Seed가 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}