package com.joeware.android.gpulumera.account.wallet.transaction.receive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletReveiveBinding
import com.joeware.android.gpulumera.util.AccountUtil
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletReceiveDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletReceiveDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, tokenType: WalletTokenType) = manager?.let {
            WalletReceiveDialog().apply { arguments = Bundle().apply { putString("type", tokenType.name) }}.show(it, TAG)
        }
    }

    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)

    private lateinit var binding: DialogWalletReveiveBinding
    private var tokenType: WalletTokenType? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletReveiveBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        tokenType = when(arguments?.getString("type")) {
            WalletTokenType.ETH.name -> WalletTokenType.ETH
            WalletTokenType.SOL.name -> WalletTokenType.SOL
            else -> null
        }
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        tokenType?.let { type ->
            when(type) {
                WalletTokenType.ETH -> {
                    binding.tvSeedTitle.text = getString(R.string.eth_wallet)
                    accountUtil.getWalletEthereumAddress()?.let { address ->
                        binding.tvSeed.text = address
                        makeAddressQRCode(address)
                    }
                }
                WalletTokenType.SOL -> {
                    binding.tvSeedTitle.text = getString(R.string.sol_wallet)
                    accountUtil.getWalletSolanaAddress()?.let { address ->
                        binding.tvSeed.text = address
                        makeAddressQRCode(address)
                    }
                }
            }
        } ?: run { dismiss() }
        binding.btnBack.setOnClickListener { dismiss() }
        binding.btnBackground.setOnClickListener { dismiss() }
        binding.btnCopy.setOnClickListener {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                clipboard.setPrimaryClip(ClipData.newPlainText("label", binding.tvSeed.text))
                Toast.makeText(context, getString(R.string.wallet_seed_copy), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeAddressQRCode(address: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(address, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val qr = barcodeEncoder.createBitmap(bitMatrix)
            binding.ivQr.setImageBitmap(qr)
        } catch (e: Exception) {

        }
    }
}