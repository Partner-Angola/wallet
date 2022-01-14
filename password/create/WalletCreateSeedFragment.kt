package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletCreateSeedBinding
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletCreateSeedFragment: CandyFragment() {

    private lateinit var binding: FragmentWalletCreateSeedBinding
    private val viewModel: WalletCreateAuthViewModel by sharedViewModel()

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletCreateSeedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.isAgree = false
        return binding.root
    }

    override fun init() {
        binding.btnCheck.setOnClickListener { binding.isAgree = !(binding.isAgree ?: false) }
        binding.btnSeedCopy.setOnClickListener {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                clipboard.setPrimaryClip(ClipData.newPlainText("label", binding.tvSeed.text))
                Toast.makeText(context, "Seed가 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSuccess.setOnClickListener { if (binding.isAgree == true) viewModel.successCreateWallet() }
        viewModel.createWallet()
    }

    override fun getLayoutRes(): Int = 0
    override fun findViews(root: View?) {}
}