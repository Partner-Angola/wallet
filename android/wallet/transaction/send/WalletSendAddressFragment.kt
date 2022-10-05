package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendAddressBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class WalletSendAddressFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendAddressBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), getString(R.string.wallet_send_address_qr_fail), Toast.LENGTH_SHORT).show()
        } else {
            binding.etAddress.setText(result.contents)
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendAddressBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        parentViewModel.notFoundAddress.observe(this) {
            Toast.makeText(requireContext(), getString(R.string.wallet_send_address_fail), Toast.LENGTH_LONG).show()
        }
    }

    override fun init() {
        binding.btnQr.setOnClickListener {
            val options = ScanOptions().apply {
                setPrompt("")
                setBeepEnabled(false)
                setOrientationLocked(false)

            }
            barcodeLauncher.launch(options)
        }

        binding.btnBack.setOnClickListener { parentViewModel.prevPage() }
        binding.btnNext.setOnClickListener {
            parentViewModel.checkAddress(binding.etAddress.text.toString())
        }

    }
}